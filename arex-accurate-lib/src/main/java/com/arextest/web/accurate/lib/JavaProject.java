package com.arextest.web.accurate.lib;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.accurate.model.*;
import com.arextest.web.accurate.util.Utils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@Configurable
@Component
public class JavaProject {
    private String rootPath;
    private final HashSet<String> javaFiles = new HashSet<>();
    private HashMap<String, FileDiffContent> diffs;
    @Getter
    private final HashMap<String, JCodeClass> classes = new HashMap<>();

    private String GitRepositoryURL;
    private String gitBranch;
    private JGitRepository gitRepository;
    private String user;
    private String token;

    @Resource
    GitConfig gitConfig;

    /**
     * 1. init project environment with new class collection.
     *
     * @param repositoryURL git repository url.
     * @param branch        branch name
     */
    public void setupJavaProject(String repositoryURL, String branch) {
        GitRepositoryURL = repositoryURL;
        gitBranch = branch;
        Path path = Paths.get(gitConfig.getGitWorkspaceDir(), com.arextest.web.accurate.lib.JGitRepository.getRepositoryDir(repositoryURL));
        rootPath = path.toString();
        user = getUserFromConfig(repositoryURL);
        token = getTokenFromConfig(repositoryURL);

        gitRepository = new JGitRepository(GitRepositoryURL, rootPath, gitBranch);
        gitRepository.buildJGitRepository(user, token);
    }

    /**
     * 获取用户名和Token
     * 逻辑是如果是github.com上,就读配置文件中github有关的字段
     * 否则就读gitlab的字段
     *
     * @param urlString git url
     */
    public String getUserFromConfig(String urlString) {
        if (urlString.contains("github.com")) {
            return gitConfig.getGitUser();
        } else {
            return gitConfig.getGitLocalUser();
        }
    }

    public String getTokenFromConfig(String urlString) {
        if (urlString.contains("github.com")) {
            return gitConfig.getGitToken();
        } else {
            return gitConfig.getGitLocalToken();
        }
    }

    /**
     * 1. git pull
     * 2. get project all git commits.
     * 3. 跟当前时间大于2个月间隔的, 则不在保存
     *
     * @return all git commits
     */
    private static final long TWO_MONTHS_IN_SECONDS = 60L * 60L * 24L * 60L;

    public List<RevCommit> revCommits(String userName, String token) {
        try {
            gitRepository.pullRepository(userName, token);
            List<RevCommit> listCommits = new LinkedList<>();
            Iterable<RevCommit> iterable = gitRepository.revisions();
            while (iterable.iterator().hasNext()) {
                RevCommit curCommit = iterable.iterator().next();
                if ((Instant.now().getEpochSecond() - curCommit.getCommitTime()) <= TWO_MONTHS_IN_SECONDS)
                    listCommits.add(curCommit);
                else
                    break;
            }
            return listCommits;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
            return null;
        }
    }

    public void setGitDiffs(HashMap<String, FileDiffContent> diffTable) {
        diffs = diffTable;
        LOGGER.info("[diff-Files]");
        if (diffs == null)
            return;
        for (String fileKey : diffTable.keySet()) {
            LOGGER.info(fileKey);
        }
    }

    /**
     * Parse all file to ast collection.
     * 编译一个Java文件,第一次编译, 取得接口列表, 方法表
     *
     * @param fileName    java file full name
     * @param fileContent file content of java
     */
    public void ScanOneJavaCode(String fileName, String fileContent) {
        if (fileName.contains("src/test/") || fileName.contains("src\\test\\"))
            return;

        CompilationUnit cu = StaticJavaParser.parse(fileContent);
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            try {
                JCodeClass codeClass = new JCodeClass(cu, fileName, cls);
                String uniClassName = codeClass.generateClassKey();
                cls.getImplementedTypes().forEach(implType -> {
                    codeClass.getInterfaces().add(implType.getName().toString());
                });
                codeClass.setMethodTable(cls.getMethods());
                classes.put(uniClassName, codeClass);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * 编译字符串,获取CompilationUnit
     * ReflectionTypeSolver()
     *
     * @param content java file code
     * @return CompilationUnit or null
     */
    public CompilationUnit compileJavaString(String content) {
        if (content.isEmpty())
            return null;

        // 创建JavaParser对象，并使用JavaParser Symbol Solver解析符号信息
        JavaParser javaParser = new JavaParser();
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(new CombinedTypeSolver());
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        // 使用JavaParser解析Java源文件，并获取其中的所有函数调用
        try {
            if (javaParser.parse(content).getResult().isPresent())
                return javaParser.parse(content).getResult().get();
        } catch (Exception e) {
            LOGGER.info(e.toString());
        }
        return null;
    }

    /**
     * JavaParser从一个java文本中,找到符合行号要求的方法名列表
     * 通过Visitor查询的
     *
     * @param codeDiff diffs
     * @return JCodeMethod list
     */
    public List<JCodeMethod> searchMethodNamesByRanges(FileDiffContent codeDiff) {
        String javaContent = codeDiff.getContent();
        List<LineRange> ranges = codeDiff.getEditedLineRangeList();

        CompilationUnit cu = compileJavaString(javaContent);
        if (cu == null)
            return null;

        // 新代码中找
        HashMap<String, JCodeMethod> mdSet = new HashMap<>();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration mD, Void arg) {
                if (!(mD.getBegin().isPresent() && mD.getEnd().isPresent()))
                    return;
                int startLine = mD.getBegin().get().line;
                int endLine = mD.getEnd().get().line;
                for (LineRange oneRange : ranges) {
                    if (oneRange.inRange(startLine, endLine)) {
                        JCodeMethod jCodeMethod = JCodeMethod.covertToJCodeMethod(mD);
                        jCodeMethod.setChangedCode(oneRange.getLogs());
                        mdSet.put(JCodeMethod.getMethodFullName(mD), jCodeMethod);
                        break;
                    }
                }
                super.visit(mD, arg);
            }
        }, null);

        // 旧代码中找, 存在问题: 如果是格式改了, 比如换行了, 这样获取代码,其实是没有变化的
        cu = compileJavaString(codeDiff.getOldContent());
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration oldMD, Void arg) {
                String queryName = JCodeMethod.getMethodFullName(oldMD);
                if (mdSet.containsKey(queryName)) {
                    mdSet.get(queryName).setOldDeclare(oldMD.toString());
                }
                super.visit(oldMD, arg);
            }
        }, null);

        return new LinkedList<>(mdSet.values());
    }


    /**
     * 第一轮编译: 扫描和记录
     * 变更标识传入 TODO 变更代码处理
     *
     * @throws IOException 文件找不到
     */
    public HashMap<String, JCodeClass> scanWholeProject() throws IOException {
        classes.clear();

        HashMap<String, String> codeFiles = new HashMap<>();
        Utils.findJavaFilesByRoot(rootPath, codeFiles);
        javaFiles.addAll(codeFiles.keySet());

        for (String javaCodeFileName : javaFiles) {
            ScanOneJavaCode(javaCodeFileName, codeFiles.get(javaCodeFileName));
        }
        return classes;
    }

    /**
     * compile all java files and build Object-Oriented
     * reCompare to analysis relation between classes.
     *
     * @throws IOException file not exist
     */
    public void compileProject() throws IOException, XmlPullParserException {
        File pomFile = new File(rootPath + "\\pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model parentModule = reader.read(new FileReader(pomFile));
        List<String> modules = parentModule.getModules();  // 获取子模块列表
        MavenProject mainProject = new MavenProject(parentModule); // 构建父项目对象,没有用到

        ArtifactUtils utils = new ArtifactUtils();

        // 收集ROOT项目依赖
        List<Dependency> dependencies = parentModule.getDependencies();
        List<File> dependenciesList = new LinkedList<>();
        utils.resolve(dependencies, dependenciesList);

        // 收集子项目依赖
        for (String module : modules) {
            File modulePomFile = new File(pomFile.getParentFile(), module + "/pom.xml");
            Model moduleModel = reader.read(new FileReader(modulePomFile));
            MavenProject moduleProject = new MavenProject(moduleModel);
            System.out.println(modulePomFile.getParentFile().getAbsolutePath());

            List<Dependency> dependencyModels = moduleProject.getDependencies();
            utils.resolve(dependencyModels, dependenciesList);
        }
        Set<File> dependencySet = new HashSet<>(dependenciesList);

        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
//        reflectionTypeSolver.setParent(reflectionTypeSolver);

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);

        for (File dependItem : dependencySet) {
            combinedTypeSolver.add(new JarTypeSolver(dependItem));
        }
        for (String codeItem : javaFiles) {
            File fileItem = new File(codeItem);
            if (!fileItem.exists()) {
                LOGGER.error(codeItem);
                continue;
            }
            combinedTypeSolver.add(new JavaParserTypeSolver(fileItem));
        }

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser
                .getParserConfiguration()
                .setSymbolResolver(symbolSolver);
        buildMethodTable();
    }

    /**
     * 搜索和构建函数表
     * 第一轮编译结束后,应该是每个类都有了基础的函数表
     */
    private void buildMethodTable() {
        for (String className : classes.keySet()) {
            JCodeClass codeClass = classes.get(className);
            String parentName = codeClass.getParentName();
            while (!(StringUtils.isEmpty(parentName) ||
                    "Object".equals(parentName))) {
                JCodeClass pClass = classes.getOrDefault(parentName, null);
                if (pClass == null)
                    break;
                codeClass.setMethodTable(pClass.getMethodTable());
                parentName = pClass.getParentName();
            }
        }
    }

    /**
     * 查询SpringBoot的Controller
     */
    public List<MethodTracing> searchCallGraphBasedSpringAnnotation() {
        HashSet<String> methodAnnotations = new HashSet<>();
        methodAnnotations.add("PostMapping");
        methodAnnotations.add("GetMapping");
        methodAnnotations.add("DeleteMapping");
        methodAnnotations.add("PutMapping");
        methodAnnotations.add("PatchMapping");
        methodAnnotations.add("RequestMapping");

        List<String> classAnnotations = new LinkedList<>();
        classAnnotations.add("Controller");
        classAnnotations.add("RestController");

        return findMethodsCallGraphByAnnotation(classAnnotations, methodAnnotations);
    }

    /**
     * Visitor模式访问函数调用树入口
     *
     * @param classAnnotations  annotation list
     * @param methodAnnotations method annotation list
     * @return list tracing
     */
    public List<MethodTracing> findMethodsCallGraphByAnnotation(List<String> classAnnotations, HashSet<String> methodAnnotations) {
        if (classAnnotations.size() == 0 || methodAnnotations.size() == 0) {
            return null;
        }
        HashSet<String> annotations = new HashSet<>(classAnnotations);
        List<MethodTracing> results = new LinkedList<>();
        for (String oneClassName : classes.keySet()) {
            JCodeClass curJCodeClass = classes.get(oneClassName);
            curJCodeClass.getTypeDeclaration().getAnnotations().forEach(annotation -> {
                if (annotations.contains(annotation.getName().toString())) {
                    curJCodeClass.getTypeDeclaration().getMethods().forEach(method -> {
                        method.getAnnotations().forEach(mAnnotation -> {
                            if (methodAnnotations.contains(mAnnotation.getName().toString())) {
                                MethodTracing oneTrace = traceMethod(curJCodeClass, method);
                                results.add(oneTrace);
                            }
                        });
                    });
                }
            });
        }
        return results;
    }

    /**
     * 根据类名和函数名,查询其静态调用链
     *
     * @param className  完整的类名
     * @param methodName 完成的函数名
     * @return List
     */
    public List<MethodTracing> findMethodCallGraph(String className, String methodName) {
        if (className.equals("") || methodName.equals(""))
            return null;
        if (!classes.containsKey(className))
            return null;
        JCodeClass jCodeClass = classes.get(className);
        List<MethodTracing> results = new LinkedList<>();
        List<MethodDeclaration> methods = jCodeClass.getTypeDeclaration().getMethodsByName(methodName);
        for (MethodDeclaration oneMethod : methods) {
            MethodTracing oneTracing = traceMethod(jCodeClass, oneMethod);
            results.add(oneTracing);
        }
        return results;
    }

    /**
     * trace by classes, interfaces, methods
     * 多个类名+接口名,查找其符合函数条件的函数, 再查询其静态调用链
     * 外部调用未放开
     *
     * @param classNames name of class
     * @param interfaces name of interface
     * @param methods    name of method
     * @return lists
     */
    public List<MethodTracing> findMethodsCallGraph(List<String> classNames, List<String> interfaces, List<String> methods) {
        List<MethodTracing> results = new LinkedList<>();

        List<JCodeClass> classList = new LinkedList<>();
        for (String oneClass : classNames) {
            if (classes.containsKey(oneClass))
                classList.add(classes.get(oneClass));
        }

        if (interfaces.size() > 0) {
            List<JCodeClass> interfaceClass = findClassByInterface(interfaces);
            if ((interfaceClass != null) && (!interfaceClass.isEmpty()))
                classList.addAll(interfaceClass);
        }

        if (methods.size() != 0) {
            for (JCodeClass aClass : classList) {
                for (String method : methods) {
                    List<MethodDeclaration> methodList = aClass.getTypeDeclaration().getMethodsByName(method);
                    if (methodList == null || methodList.size() == 0)
                        continue;
                    for (MethodDeclaration methodDeclaration : methodList) {
                        MethodTracing trace = traceMethod(aClass, methodDeclaration);
                        if (trace != null)
                            results.add(trace);
                    }
                }
            }
        } else {
            for (JCodeClass aClass : classList) {
                HashMap<String, MethodDeclaration> methodList = aClass.getMethodTable();
                for (MethodDeclaration methodDeclaration : methodList.values()) {
                    MethodTracing trace = traceMethod(aClass, methodDeclaration);
                    if (trace != null)
                        results.add(trace);
                }
            }
        }

        return results;
    }

    /**
     * 生成函数的唯一标识名, 加上类名
     *
     * @param jCodeClass        类
     * @param methodDeclaration 函数
     * @return 类名:函数名:参数名
     */
    public static String generateMethodDeclarationKey(JCodeClass jCodeClass, MethodDeclaration methodDeclaration) {
        return jCodeClass.getFullName() +
                ":" +
                generateMethodDeclarationKey(methodDeclaration);
    }

    /**
     * 生成函数名唯一标识名
     *
     * @param methodDeclaration instance get unique name
     * @return string
     */
    public static String generateMethodDeclarationKey(MethodDeclaration methodDeclaration) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodDeclaration.getName().toString());
        for (Parameter onePara : methodDeclaration.getParameters()) {
            stringBuilder.append(":");
            stringBuilder.append(onePara.getName().toString());
        }
        return stringBuilder.toString();
    }

    /**
     * 分析函数调用树的入口函数
     * findMethodCallGraph和findMethodsCallGraph,调用它
     *
     * @param jCodeClass
     * @param methodDeclaration
     * @return
     */
    private MethodTracing traceMethod(JCodeClass jCodeClass, MethodDeclaration methodDeclaration) {
        Queue<ImmutablePair<JCodeClass, MethodDeclaration>> declarationQueue = new LinkedList<>();
        LinkedList<String> allListCalls = new LinkedList<>();
        declarationQueue.add(new ImmutablePair(jCodeClass, methodDeclaration));
        String itemKey = generateMethodDeclarationKey(jCodeClass, methodDeclaration);
        HashMap<String, ImmutablePair<JCodeClass, MethodDeclaration>> needTraceMethods = new HashMap<>();
        needTraceMethods.put(itemKey, new ImmutablePair(jCodeClass, methodDeclaration));

        // 查询调用树
        traceMethodCallExpr(declarationQueue, allListCalls, needTraceMethods);

        // 查询结束填数据
        MethodTracing methodTracing = new MethodTracing();
        methodTracing.setClassName(jCodeClass.getFullName());
        methodTracing.setMethodName(methodDeclaration.getNameAsString());
        methodTracing.setMappingURL(methodDeclaration.getAnnotations().toString());
        methodTracing.getListFullMethodInvocations().addAll(allListCalls);
        methodTracing.getListCodeMethods().addAll(needTraceMethods.keySet());
        return methodTracing;
    }

    /**
     * 递归实现调用分析
     *
     * @param declarationQueue
     * @param allListCalls
     * @param needTraceMethods
     */
    public void traceMethodCallExpr(Queue<ImmutablePair<JCodeClass, MethodDeclaration>> declarationQueue,
                                    LinkedList<String> allListCalls,
                                    HashMap<String, ImmutablePair<JCodeClass, MethodDeclaration>> needTraceMethods) {
        ImmutablePair<JCodeClass, MethodDeclaration> oneItem = declarationQueue.peek();

        List<MethodCallExpr> methodCallExprList = new ArrayList<>();
        oneItem.getRight().accept(new MethodCallExprVisitor(), methodCallExprList);

        for (MethodCallExpr methodCallExpr : methodCallExprList) {
            allListCalls.add(methodCallExpr.toString());
            ResolvedMethodDeclaration oriDeclaration;
            try {
                oriDeclaration = methodCallExpr.resolve();
                if (oriDeclaration == null)
                    continue;
            } catch (UnsolvedSymbolException e) {
                LOGGER.info(e.toString());
                continue;
            } catch (Exception e) {
                LOGGER.debug(e.toString());
                continue;
            }

            StringBuilder resolveTypeName = new StringBuilder();
            resolveTypeName.append(oriDeclaration.getPackageName());
            resolveTypeName.append(".");
            resolveTypeName.append(oriDeclaration.getClassName());
            LOGGER.info(resolveTypeName.toString());

            JCodeClass codeClass = classes.getOrDefault(resolveTypeName.toString(), null);
            if (codeClass == null)
                continue;

            List lists = codeClass.getTypeDeclaration().getMethodsByName(oriDeclaration.getName());
            if (lists.size() == 0)
                continue;

            // 判断参数是否相同, 未完成
            MethodDeclaration oneMethod = null;
            if (lists.size() == 1) {
                oneMethod = (MethodDeclaration) lists.get(0);
            } else {
                oneMethod = (MethodDeclaration) lists.get(0);
            }
            // 如果没有包含过这个method, 则放到队列,并记录跟踪列表
            String newKey = generateMethodDeclarationKey(codeClass, oneMethod);
            // debug code
            findMethodByMethodCall(codeClass, methodCallExpr);
            if (!needTraceMethods.containsKey(newKey)) {
                declarationQueue.add(new ImmutablePair(codeClass, oneMethod));
                needTraceMethods.put(newKey, new ImmutablePair(codeClass, oneMethod));
            }
        }
    }

    /**
     * 在codeClass里边,根据expr表达式查找实际的定义函数
     *
     * @param codeClass
     * @param expr
     * @return
     */
    private MethodDeclaration findMethodByMethodCall(JCodeClass codeClass, MethodCallExpr expr) {
        if (codeClass == null || expr == null)
            return null;

        List<MethodDeclaration> methods = codeClass.getTypeDeclaration().getMethodsByName(expr.getNameAsString());
        for (MethodDeclaration oneMethod : methods) {
            if (oneMethod.getParameters().size() != expr.getArguments().size())
                continue;
            if (oneMethod.getParameters().size() == 0)
                return oneMethod;
            for (int i = 0; i < oneMethod.getParameters().size(); i++) {
                LOGGER.info(oneMethod.getParameter(i).getName().getIdentifier());
                LOGGER.info(expr.getArguments().get(i).toString());
            }
        }
        return null;
    }

    /**
     * 通过接口名查找类名
     *
     * @param interfaces Interface类名
     * @return
     */
    public List<JCodeClass> findClassByInterface(List<String> interfaces) {
        if (interfaces == null || interfaces.size() == 0)
            return null;

        List<JCodeClass> classList = new LinkedList<>();
        HashSet<String> interfaceSet = new HashSet<>(interfaces);
        Set<String> result = new HashSet<String>();
        for (String oneClass : classes.keySet()) {
            JCodeClass jClass = classes.get(oneClass);
            result.clear();
            result.addAll(jClass.getInterfaces());
            result.retainAll(interfaceSet);
            if (result.size() > 0 && classes.containsKey(oneClass))
                classList.add(classes.get(oneClass));
        }
        return classList;
    }

    /**
     * @param newRevision 最新版本commit id
     * @param oldRevision 起始版本 commit ID
     * @return map
     * @throws IOException
     */
    private HashMap<String, FileDiffContent> findCodeDiffs(final String newRevision, final String oldRevision) throws IOException {
        try {
            gitRepository.buildJGitRepository(user, token);
            RevCommit newCommit = gitRepository.getRevCommitForCommitSHA(newRevision);
            RevCommit oldCommit = gitRepository.getRevCommitForCommitSHA(oldRevision);
            return gitRepository.findDiffEntryBetweenTwoRevisions(newCommit, oldCommit);
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
            return null;
        }
    }

    /**
     * 根据代码变化找到变化的methods
     *
     * @param newRevision 开始版本commit id
     * @param oldRevision 结束版本 commit ID
     * @return map
     * @throws IOException
     */
    public List<JCodeMethod> scanCodeDiffMethods(final String newRevision, final String oldRevision) {
        try {
            diffs = findCodeDiffs(newRevision, oldRevision);
            if (diffs == null || diffs.isEmpty())
                return null;

            List<JCodeMethod> methods = new ArrayList<>();
            for (String classPath : diffs.keySet()) {
                FileDiffContent codeDiff = diffs.get(classPath);
                List<JCodeMethod> methodList = searchMethodNamesByRanges(codeDiff);
                if (methodList != null && !methodList.isEmpty())
                    methods.addAll(methodList);
            }

            return methods;
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
            return null;
        }
    }

    /**
     * 只传一个commit, 获取其上一个commitid
     * 然后去比对, 已经减去1,但总觉得不是很严谨, parents就是指向上一个吗, 没有分支信息呢
     *
     * @param theLatestCommitID 传入的ID
     * @return
     */
    public List<JCodeMethod> scanCodeDiffMethods(final String theLatestCommitID) {
        List<RevCommit> revCommitList = revCommits(user, token);
        String prevCommit = "";
        for (int i = 0; i < revCommitList.size() - 1; i++) {
            if (revCommitList.get(i).getName().equals(theLatestCommitID))
                prevCommit = revCommitList.get(i + 1).getName();
        }
        if (prevCommit.equals(""))
            return null;
        return scanCodeDiffMethods(theLatestCommitID, prevCommit);
    }

    /**
     * MethodCallExprVisitor类用于访问函数声明中的所有函数调用
     */
    static class MethodCallExprVisitor extends VoidVisitorAdapter<List<MethodCallExpr>> {
        @Override
        public void visit(MethodCallExpr n, List<MethodCallExpr> arg) {
            arg.add(n);
            super.visit(n, arg);
        }
    }


    // /**
    //  * webhook接收gitlab的EventMergeRequest
    //  * 1 没有代码下载代码
    //  * 2 有代码, 则拉取最新代码 git pull
    //  * 2. 有历史merge, 则新旧两个commit比较变更函数
    //  * 3. 分析新commit的函数(是否记录再说)
    //  * TODO : 异步实现, 当检查查询符合要求后就返回success
    //  *
    //  * @param gitURL git url
    //  * @param
    //  * @return SUCCESS OR FAIL MESSAGE
    //  */
    // public Response DoEventMergeRequestOfGitlab(String gitURL, String commitID, String branch) throws GitAPIException, IOException, ArtifactNotFoundException, XmlPullParserException, ArtifactResolutionException {
    //     setupJavaProject(gitURL, branch);
    //     gitRepository.buildJGitRepository(user, token);
    //
    //     List<JCodeMethod> result = scanCodeDiffMethods(commitID);
    //     // just for walk not compile
    //     // HashMap<String, JCodeClass> allClasses = scanWholeProject();
    //     // List<JCodeMethod> allMethods = getJCodeMethods();
    //     // TODO: save class and method code  to DB
    //
    //     Response res = new Response();
    //     res.setData(result);
    //     res.setErrorCode(20000);
    //     return res;
    //
    // }

    /**
     * Clone Git 代码仓库
     * shell方式拉的代码,能成功,但不保证,返回错误码也怪的很
     *
     * @param request
     * @return
     */
    public Response DoGitClone(GitBasicRequest request) {
        setupJavaProject(request.getRepositoryURL(), request.getBranch());
        if (gitRepository != null) {
            return ResponseUtils.successResponse("Done");
        }
        return ResponseUtils.exceptionResponse("clone failed");
    }

    /**
     * delete local git dir.
     * 不要搞,目前
     *
     * @param url git repository url.
     * @return Response: success; false: failed;
     */
    public Response DoCleanProject(String url) {
        Path path = Paths.get(gitConfig.getGitWorkspaceDir(), JGitRepository.getRepositoryDir(url));

        File dirProject = new File(path.toString());
        if (!dirProject.exists())
            return ResponseUtils.errorResponse("project not exist", ResponseCode.REQUESTED_PARAMETER_INVALID);

        if (Utils.deleteFileOrDirectory(dirProject)) {
            return ResponseUtils.successResponse("done");
        } else {
            return ResponseUtils.exceptionResponse("delete failed");
        }
    }

    /**
     * 获取最新的所有的Commits信息
     *
     * @param request GitBasicRequest
     * @return CommitsResponse
     */
    public CommitsResponse DoListCommits(GitBasicRequest request) {
        setupJavaProject(request.getRepositoryURL(), request.getBranch());
        List<RevCommit> revCommitList = revCommits(user, token);

        List<CommitInfo> results = new LinkedList<>();
        for (RevCommit commit : revCommitList) {
            if (commit == null)
                continue;
            CommitInfo commitInfo = new CommitInfo();
            commitInfo.fullMessage = commit.getFullMessage();
            commitInfo.commitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(commit.getCommitTime() * 1000L));
            commitInfo.commitID = commit.getName();
            results.add(commitInfo);
        }

        CommitsResponse commitsResponse = new CommitsResponse();
        commitsResponse.setResult("success");
        commitsResponse.setCommits(results);
        return commitsResponse;
    }

    /**
     * 将类中的所有MethodDeclaration转到JCodeMethod,并返回
     *
     * @return
     */
    public List<JCodeMethod> getJCodeMethods() {
        List<JCodeMethod> listMethods = new ArrayList<>();
        for (JCodeClass oneClass : classes.values()) {
            for (MethodDeclaration oneMethod : oneClass.getMethodTable().values()) {
                listMethods.add(JCodeMethod.covertToJCodeMethod(oneMethod));
            }
        }
        return listMethods;
    }

    /**
     * 静态分析之Spring的调用树分析(最新代码)
     * 是否用得着以后再说
     *
     * @param gitURL git http url
     * @param branch branch string
     * @return MethodTracing list
     */
    public List<MethodTracing> DoTraceSpring(String gitURL, String branch) {
        try {
            setupJavaProject(gitURL, branch);
            scanWholeProject();
            compileProject();
            return searchCallGraphBasedSpringAnnotation();
        } catch (Exception e) {
            LOGGER.error(e.toString());
            e.printStackTrace();
        }
        return null;
    }

}

