package com.arextest.web.accurate.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Utils {

    /**
     * 获取当前代码库的
     * @param repository
     * @return
     * @throws IOException
     */
    public static String getCurrentCommitId(Repository repository) throws IOException {
        try (Git git = new Git(repository)) {
            ObjectId headId = repository.resolve("HEAD");
            return headId.getName();
        }
    }

    /**
     * 获取在某指定的commit上, 某个文件的原文
     * @param repository
     * @param commit
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getRawText(Repository repository, RevCommit commit, String filePath) throws IOException {
        try (TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, commit.getTree())) {
            if (treeWalk != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                repository.open(treeWalk.getObjectId(0)).copyTo(outputStream);
                return outputStream.toString();
            }
        }
        return null;
    }

    public static List<Dependency> getDependenciesFromPom(String pomFilePath) throws Exception {
        File pomFile = new File(pomFilePath);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFile));
        return model.getDependencies();
    }

    /**
     * 扫描文件清单
     *
     * @param modelRootDir model root dir
     * @throws IOException
     */
    public static void findJavaFilesByRoot(String modelRootDir, HashMap<String, String> filesMap) throws IOException {
        File fileInstance = new File(modelRootDir);
        findJavaFilesInDir(fileInstance, filesMap);
    }

    /**
     * read file content into a string
     */
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1024);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * 扫描目录下所有.java文件, 存储的是<绝对路径,文件内容>
     * String rootDir TODO: 用不着了吧, fullPathName.substring(rootDir.length())
     *
     * @param f        文件实例
     * @param filesMap hashmap of files.
     * @throws IOException file not exist?
     */
    public static void findJavaFilesInDir(File f, HashMap<String, String> filesMap) throws IOException {
        for (File fileInstance : Objects.requireNonNull(f.listFiles())) {
            if (fileInstance.isFile()) {
                String fullPathName = fileInstance.getAbsolutePath();
                String suffix = FilenameUtils.getExtension(fullPathName);
                if (!"java".equals(suffix))
                    continue;
                filesMap.put(fullPathName, readFileToString(fullPathName));
            } else if (fileInstance.isDirectory()) {
                findJavaFilesInDir(fileInstance, filesMap);
            }
        }
    }

    /**
     * 删除目录和文件
     * @param file
     * @return
     */
    public static boolean deleteFileOrDirectory(File file) {
        if (null != file) {
            if (!file.exists()) {
                return true;
            }
            int i;
            if (file.isFile()) {
                boolean result = file.delete();
                for (i = 0; !result && i++ < 3; result = file.delete()) {
                    System.gc();
                }
                return result;
            }

            File[] files = file.listFiles();
            if (null != files) {
                for (i = 0; i < files.length; ++i) {
                    deleteFileOrDirectory(files[i]);
                }
            }
            return file.delete();
        }
        return true;
    }

    public static Boolean compareTwoClass(Object class1, Object class2, List<Field> ignoreFieldList) throws ClassNotFoundException, IllegalAccessException {
        //动态的获取指定对象的class
        Class<?> clazz1 = class1.getClass();
        Class<?> clazz2 = class2.getClass();
        // 获取类中所有的属性(public、protected、default、private)，但不包括继承的属性，返回 Field 对象的一个数组
        Field[] field1 = clazz1.getDeclaredFields();
        Field[] field2 = clazz2.getDeclaredFields();

        //遍历属性列表field1
        for (int i = 0; i < field1.length; i++) {
            //遍历属性列表field2
            for (int j = 0; j < field2.length; j++) {
                //如果field1[i]属性名与field2[j]属性名内容相同

                if (field1[i].getName().equals(field2[j].getName())) {
                    Boolean checkIgnoreField = true;
                    if (ignoreFieldList != null && ignoreFieldList.size() > 0) {
                        for (int ignoreFieldNum = 0; ignoreFieldNum < ignoreFieldList.size(); ignoreFieldNum++) {
                            //如果要忽略的类型里存在，就跳过这个方法
                            if (field1[i].getName().equals(ignoreFieldList.get(ignoreFieldNum))) {
                                checkIgnoreField = false;
                            }
                        }
                        if (checkIgnoreField) {
                            //调过本次循环的下面语句执行
                            continue;
                        }

                        if (!compareTwo(field1[i], field2[j], class1, class2)) {
                            return false;
                        }
                        break;
                    } else {
                        if (!compareTwo(field1[i], field2[j], class1, class2)) {
                            return false;
                        }
                        break;

                    }
                }


            }
        }
        return true;

    }

    /**
     * 对比两个数据是否内容相同
     *
     * @param
     * @return boolean类型
     */
    public static boolean compareTwo(Field field1, Field field2, Object class1, Object class2) throws IllegalAccessException {

        //让我们可以访问私有变量的值
        field1.setAccessible(true);
        field2.setAccessible(true);
        //如果field1[i]属性值与field2[j]属性值内容不相同
        //为了不重写equals方法目前只能比较基础的类型
        if ("java".equals(field1.getGenericType().toString())) {

        }
        //返回该类下面对应的该属性值，并返回结果
        Object object1 = field1.get(class1);
        Object object2 = field2.get(class2);
        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null && object2 != null) {
            return false;
        }
        if (object1.equals(object2)) {
            return true;
        }
        return false;
    }


}