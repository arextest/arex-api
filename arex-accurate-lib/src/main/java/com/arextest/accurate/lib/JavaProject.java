package com.arextest.accurate.lib;

import com.arextest.accurate.model.*;
import com.arextest.accurate.util.SpringUtil;
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.EventMergeRequest;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.arextest.accurate.util.Utils.*;

@Slf4j
public class JavaProject {
    private String rootPath;
    private final HashSet<String> javaFiles = new HashSet<>();
    private HashMap<String, CodeDiff> diffs;
    @Getter
    private final HashMap<String, JCodeClass> classes = new HashMap<>();

    private String GitRepositoryURL;
    private String gitBranch;
    private com.arextest.accurate.lib.JGitRepository JGitRepository;

    @Getter
    private final GitConfig gitConfig;

    /**
     * init config
     */
    public JavaProject() {
        gitConfig = (GitConfig) SpringUtil.getBean("gitConfig");
    }

    /**
     * init project environment with new class collection.
     *
     * @param repositoryURL git repository url.
     * @param branch        branch name
     */
    public void initialProjectConfig(String repositoryURL, String branch) {
        GitRepositoryURL = repositoryURL;
        gitBranch = branch;
        Path path = Paths.get(gitConfig.getGitWorkspaceDir(), com.arextest.accurate.lib.JGitRepository.getRepositoryDir(repositoryURL));
        rootPath = path.toString();
    }


    /**
     * init git source dir. pull code.
     *
     * @return true: init success. false: init failed.
     */
    public boolean initGitEnvironment(String commitID) {
        try {
            JGitRepository = new JGitRepository(GitRepositoryURL, rootPath, gitBranch);
            return JGitRepository.initJGitRepository(commitID);
        } catch (Exception e) {
            LOGGER.info(e.toString());
        }
        return false;
    }

    /**
     * git clone repository.
     *
     * @param userName git username
     * @param password git token
     * @return git result description.
     */
    public JGitRepository cloneGitRepository(String userName, String password) {
        try {
            JGitRepository gitRepository = new JGitRepository(GitRepositoryURL, rootPath, gitBranch);
            gitRepository.cloneRepositoryShell(gitConfig.getGitWorkspaceDir(), userName, password);
            return gitRepository;
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return null;
    }

    /**
     * 获取用户名和Token
     * 逻辑是如果是github.com上,就读配置文件中github有关的字段
     * 否则就读gitlab的字段
     *
     * @param urlString
     */
    public Pair<String, String> getGitRepositoryUserToken(String urlString) {
        if (urlString.contains("github.com")) {
            return Pair.of(getGitConfig().getGitUser(), getGitConfig().getGitToken());
        } else {
            return Pair.of(getGitConfig().getGitLocalUser(), getGitConfig().getGitLocalToken());
        }
    }

    /**
     * 1. git pull
     * 2. get project all git commits.
     *
     * @return all git commits
     */
    public List<RevCommit> revCommits(String userName, String token) {
        try {
            JGitRepository.pullRepository(userName, token);
            List<RevCommit> listCommits = new LinkedList<>();
            Iterable<RevCommit> iterable = JGitRepository.revisions();
            while (iterable.iterator().hasNext()) {
                listCommits.add(iterable.iterator().next());
            }
            return listCommits;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
            return null;
        }
    }

    public void setGitDiffs(HashMap<String, CodeDiff> diffTable) {
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
     * @param fileName
     * @param fileContent
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
     * @param content
     * @return
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
            return javaParser.parse(content).getResult().get();
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return null;
        }

    }

    /**
     * JavaParser从一个java文本中,找到符合行号要求的方法名列表
     * 通过Visitor查询的
     *
     * @param codeDiff diffs
     * @return
     */
    public List<JCodeMethod> searchMethodNamesByRanges(CodeDiff codeDiff) {
        String javaContent = codeDiff.getContent();
        List<LineRange> ranges = codeDiff.getEditedLineRangeList();

        CompilationUnit cu = compileJavaString(javaContent);
        if (cu == null)
            return null;

        HashMap<String, MethodDeclaration> mdSet = new HashMap<>();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration mD, Void arg) {
                int startLine = mD.getBegin().get().line;
                int endLine = mD.getEnd().get().line;
                for (LineRange oneRange : ranges) {
                    if (oneRange.inRange(startLine, endLine)) {
                        mdSet.put(JCodeMethod.getMethodFullName(mD), mD);
                        break;
                    }
                }
                super.visit(mD, arg);
            }
        }, null);

        List<JCodeMethod> result = new ArrayList<>();
        cu = compileJavaString(codeDiff.getOldContent());
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration oldMD, Void arg) {
                String queryName = JCodeMethod.getMethodFullName(oldMD);
                if (mdSet.containsKey(queryName)) {
                    JCodeMethod jcm = JCodeMethod.covertToJCodeMethod(mdSet.get(queryName));
                    jcm.setOldDeclare(oldMD.toString());
                    result.add(jcm);
                }
                super.visit(oldMD, arg);
            }
        }, null);

        return result;
    }


    /**
     * 第一轮编译: 扫描和记录
     * 变更标识传入 TODO 变更代码处理
     *
     * @throws IOException 文件找不到
     */
    public HashMap<String, JCodeClass> scanProject() throws IOException {
        classes.clear();

        HashMap<String, String> codeFiles = new HashMap<>();
        findJavaFilesByRoot(rootPath, codeFiles);
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
    public void compileProject() throws IOException, XmlPullParserException, ArtifactNotFoundException, ArtifactResolutionException {
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
     * trace by spring boot's annotation.
     *
     * @return lists
     */
    public List<MethodTracing> traceCallGraphInSpring() {
        return searchCallGraphBasedSpringAnnotation();
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

        return findTracingsByAnnotation(classAnnotations, methodAnnotations);
    }

    /**
     * Visitor模式访问函数调用树入口
     *
     * @param classAnnotations  annotation list
     * @param methodAnnotations method annotation list
     * @return list tracing
     */
    public List<MethodTracing> findTracingsByAnnotation(List<String> classAnnotations, HashSet<String> methodAnnotations) {
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
                        ((MethodDeclaration) method).getAnnotations().forEach(methodAnno -> {
                            if (methodAnnotations.contains(methodAnno.getName().toString())) {
                                MethodTracing oneTrace = traceMethod(curJCodeClass, (MethodDeclaration) method);
                                results.add(oneTrace);
                            }
                        });
                    });
                }
            });
        }
        return results;
    }

    public List<MethodTracing> findCallGraph(String className, String methodName) {
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
     *
     * @param classes    name of class
     * @param interfaces name of interface
     * @param methods    name of method
     * @return lists
     */
    public List<MethodTracing> DoBuildCallGraph(List<String> classes, List<String> interfaces, List<String> methods) {
        List<MethodTracing> results = new LinkedList<>();

        List<JCodeClass> result = findClass(classes);
        if (interfaces.size() > 0) {
            List<JCodeClass> interfaceClass = findClassByInterface(interfaces);
            if ((interfaceClass != null) && (!interfaceClass.isEmpty()))
                result.addAll(interfaceClass);
        }

        if (methods.size() != 0) {
            for (JCodeClass aClass : result) {
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
            for (JCodeClass aClass : result) {
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
     * @param methodDeclaration
     * @return
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
     *
     * @param jCodeClass
     * @param methodDeclaration
     * @return
     */
    public MethodTracing traceMethod(JCodeClass jCodeClass, MethodDeclaration methodDeclaration) {
        Queue<ImmutablePair<JCodeClass, MethodDeclaration>> declarationQueue = new LinkedList<>();
        LinkedList<String> allListCalls = new LinkedList<>();
        declarationQueue.add(new ImmutablePair(jCodeClass, methodDeclaration));
        String itemKey = generateMethodDeclarationKey(jCodeClass, methodDeclaration);

        HashMap<String, ImmutablePair<JCodeClass, MethodDeclaration>> needTraceMethods = new HashMap<>();
        needTraceMethods.put(itemKey, new ImmutablePair(jCodeClass, methodDeclaration));

        traceMethodCallExpr(declarationQueue, allListCalls, needTraceMethods);

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
            JCodeClass codeClass = findClass(resolveTypeName.toString());
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

    public MethodDeclaration findMethodByMethodCall(JCodeClass codeClass, MethodCallExpr expr) {
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

    public List<JCodeClass> findClass(List<String> ces) {
        List<JCodeClass> classList = new LinkedList<>();
        for (String oneClass : ces) {
            if (classes.containsKey(oneClass))
                classList.add(classes.get(oneClass));
        }
        return classList;
    }

    public JCodeClass findClass(String ces) {
        return classes.getOrDefault(ces, null);
    }

    /**
     * 通过接口查找类名
     *
     * @param interfaces
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
    private HashMap<String, CodeDiff> pullCodeDiff(final String newRevision, final String oldRevision) throws IOException {
        try {
            JGitRepository.initJGitRepository(newRevision);
            return JGitRepository.findCodeChangesBetweenCommitStrings(newRevision, oldRevision);
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
            diffs = pullCodeDiff(newRevision, oldRevision);
            if (diffs == null || diffs.isEmpty())
                return null;

            List<JCodeMethod> methods = new ArrayList<>();
            for (String classPath : diffs.keySet()) {
                CodeDiff codeDiff = diffs.get(classPath);
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
     * MethodCallExprVisitor类用于访问函数声明中的所有函数调用
     */
    static class MethodCallExprVisitor extends VoidVisitorAdapter<List<MethodCallExpr>> {
        @Override
        public void visit(MethodCallExpr n, List<MethodCallExpr> arg) {
            arg.add(n);
            super.visit(n, arg);
        }
    }


    /**
     * webhook接收gitlab的EventMergeRequest
     * 1 没有代码下载代码
     * 2 有代码, 则拉取最新代码 git pull
     * 2. 有历史merge, 则新旧两个commit比较变更函数
     * 3. 分析新commit的函数(是否记录再说)
     * TODO : 异步实现, 当检查查询符合要求后就返回success
     *
     * @param request EventMergeRequest
     * @return SUCCESS OR FAIL MESSAGE
     */
    public Response DoEventMergeRequestOfGitlab(EventMergeRequest request) throws GitAPIException, IOException, ArtifactNotFoundException, XmlPullParserException, ArtifactResolutionException {
        String urlInfo = request.getUrl();
        Pair<String, String> userToken = getGitRepositoryUserToken(urlInfo);

        initialProjectConfig(urlInfo, request.getTargetBranch());
        JGitRepository gitRepository = cloneGitRepository(userToken.getLeft(), userToken.getRight());
        if (gitRepository == null) {
            return Response.exceptionResponse("clone failed");
        }

        EventCommit eventCommit = request.getLastCommit();
        String commitId = eventCommit.getId();
        RevCommit revCommit = new RevWalk(gitRepository.getGitRepo())
                .parseCommit(ObjectId.fromString(commitId));
        List<RevCommit> lr = revCommits(userToken.getLeft(), userToken.getRight());
        if (lr.size() > 1) {
            RevCommit lastCommit = lr.get(1);
            List<JCodeMethod> result = scanCodeDiffMethods(revCommit.toString(), lastCommit.toString());
            // TODO: save changed method name to DB
        }

        // just for walk not compile
        HashMap<String,JCodeClass> result = scanProject();
        // TODO: save class and method code  to DB

        return Response.successResponse();
    }

    /**
     * Clone Git 代码仓库
     * shell方式拉的代码
     *
     * @param request
     * @return
     */
    public Response DoGitClone(GitBasicRequest request) {
        String urlInfo = request.getRepositoryURL();
        Pair<String, String> userToken = getGitRepositoryUserToken(urlInfo);

        initialProjectConfig(urlInfo, request.getBranch());
        JGitRepository gitRepository = cloneGitRepository(userToken.getLeft(), userToken.getRight());
        if (gitRepository != null) {
            return Response.successResponse();
        }
        return Response.exceptionResponse("clone failed");
    }

    /**
     * delete local git dir.
     *
     * @param url git repository url.
     * @return Response: success; false: failed;
     */
    public Response DoCleanProject(String url) {
        Path path = Paths.get(gitConfig.getGitWorkspaceDir(),
                com.arextest.accurate.lib.JGitRepository.getRepositoryDir(url));

        File dirProject = new File(path.toString());
        if (!dirProject.exists())
            return Response.successResponse();

        if (deleteFileOrDirectory(dirProject)) {
            return Response.successResponse();
        } else {
            return Response.exceptionResponse("delete failed");
        }
    }

    /**
     * 获取最新的所有的Commits信息
     *
     * @param request GitBasicRequest
     * @return CommitsResponse
     */
    public CommitsResponse DoListCommits(GitBasicRequest request) {
        initialProjectConfig(request.getRepositoryURL(), "");
        if (!initGitEnvironment("")) {
            return CommitsResponse.exceptionResponse("git environment initial job is failed.");
        }

        Pair<String, String> userToken = getGitRepositoryUserToken(request.getRepositoryURL());
        List<RevCommit> lr = revCommits(userToken.getLeft(), userToken.getRight());
        List<CommitInfo> results = new LinkedList<>();
        for (RevCommit rc : lr) {
            if (rc == null)
                continue;
            CommitInfo rci = new CommitInfo();
            rci.fullMessage = rc.getFullMessage();
            rci.commitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(rc.getCommitTime() * 1000L));
            rci.commitID = rc.getName();
            results.add(rci);
        }
        CommitsResponse commitsResponse = new CommitsResponse();
        commitsResponse.setResult("success");
        commitsResponse.setCommits(results);
        return commitsResponse;
    }

    /**
     * @param request
     * @return
     */
    public GitBasicResponse DoListDiffMethods(GitBasicRequest request) {
        try {
            initialProjectConfig(request.getRepositoryURL(), "");
            if (!initGitEnvironment(request.getNewCommit()))
                return GitBasicResponse.exceptionResponse("git repository local init failed. ");

            List<JCodeMethod> result = scanCodeDiffMethods(request.getNewCommit(), request.getOldCommit());
            GitBasicResponse response = new GitBasicResponse();
            if (result.size() == 0) {
                response.setErrorCode(10000);
                response.setResult("null return");
                return response;
            }

            response.setErrorCode(20000);
            response.setResult("success");
            response.setData(result);
            return response;
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return GitBasicResponse.exceptionResponse(e.toString());
        }
    }

    /**
     * 将类中的所有MethodDeclaration转到JCodeMethod,并返回
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
     * 从源码解析并获取所有的函数信息
     *
     * @param request
     * @return
     */
    public Response DoCodeScan(GitBasicRequest request) {
        List<JCodeMethod> listMethods = getJCodeMethods();
        Response response = new Response();
        response.setData(listMethods);
        return response;
    }

    /**
     * 静态分析之Spring的调用树分析(最新代码)
     * 是否用得着以后再说
     *
     * @param gitURL
     * @param branch
     * @return
     */
    public List<MethodTracing> DoTraceSpring(String gitURL, String branch) {
        try {
            initialProjectConfig(gitURL, branch);
            if (!initGitEnvironment("")) {
                return null;
            }

            scanProject();
            compileProject();

            return traceCallGraphInSpring();
        } catch (Exception e) {
            LOGGER.error(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据被测试服务记录的CallStack分析
     * 老代码
     *
     * @param request
     * @return
     */
    public TracingResponse DoDynamicTrace(GitBasicRequest request) {
        initialProjectConfig(request.getRepositoryURL(), "");
        if (!initGitEnvironment("")) {
            return TracingResponse.exceptionResponse("init environment failed.");
        }

        Pair<String, String> userToken = getGitRepositoryUserToken(request.getRepositoryURL());
        HashMap<String, CodeDiff> diffs = null;
        try {
            List<RevCommit> revCommitList = revCommits(userToken.getLeft(), userToken.getRight());
            ImmutablePair<String, String> immutablePair = request.getCommits(request, revCommitList);
            if (!(immutablePair == null || StringUtils.isEmpty(immutablePair.getLeft()) || StringUtils.isEmpty(immutablePair.getRight())))
                diffs = pullCodeDiff(immutablePair.getLeft(), immutablePair.getRight());
            setGitDiffs(diffs);
            compileProject();

            TracingResponse tracingResponse = new TracingResponse();
            tracingResponse.setMessages(traceCallGraphInSpring());
            tracingResponse.setResult("success");
            return tracingResponse;
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return TracingResponse.exceptionResponse(e.toString());
        }
    }
}

