package com.arextest.accurate.lib;

import java.io.File;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.apache.maven.model.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;

@Slf4j
public class ArtifactUtils {
    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                LOGGER.error("Service creation failed for {} with implementation {}", type, impl, exception);
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    private static RepositorySystemSession newRepositorySystemSession(RepositorySystem repositorySystem) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        // 设置ArtifactDescriptorPolicy
        session.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(true, true));
        return session;
    }

    /**
     * 临时设置开关,暂时不管jar包依赖
     */
    private static boolean isLogicEnabled = false;

    public static void resolve(List<Dependency> dependencies, List<File> dependenciesList) {
        if (!isLogicEnabled)
            return;

        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newRepositorySystemSession(repositorySystem);

        // 遍历依赖项并下载Artifact
        for (Dependency dependency : dependencies) {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            String version = dependency.getVersion();

            DefaultArtifact artifact = new DefaultArtifact(groupId, artifactId, "", "jar", version);
            ArtifactRequest request = new ArtifactRequest();
            request.setArtifact(artifact);
            try {
                ArtifactResult result = repositorySystem.resolveArtifact(session, request);
                File artifactFile = result.getArtifact().getFile();

                // 根据需要进行处理，比如将Artifact复制到指定目录
                dependenciesList.add(artifactFile);
            } catch (ArtifactResolutionException e) {
                // 处理解析Artifact过程中的异常
                e.printStackTrace();
            }
        }
    }

    public void testMethod() {
        RepositorySystem repoSystem = newRepositorySystem();
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository("path/to/local/repo");
        session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(session, localRepo));
//        RemoteRepository centralRepo = new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/");
        // 初始化远程仓库列表
//        List<ArtifactRepository> remoteRepositories = parentProject.getRemoteArtifactRepositories();
        // 初始化本地仓库
        String localRepositoryPath = System.getProperty("user.home") + "/.m2/repository";
        DefaultArtifactRepository localRepository = new DefaultArtifactRepository("local", "file://" + localRepositoryPath, new DefaultRepositoryLayout());
    }

    public static String getJarFilePath(Dependency dependency) {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();
        String jarFileName = artifactId + "-" + version + ".jar";
        String localRepositoryPath = System.getProperty("user.home") + "/.m2/repository/";
        return localRepositoryPath + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + jarFileName;
    }


}