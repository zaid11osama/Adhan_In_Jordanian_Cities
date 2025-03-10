package com.arabbank.hdf.uam.brain.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.net.ServerSocketFactory;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Omar-Othman
 */
public class UamUtils {

	public static Set<Class<?>> scanClassesWithAnnotation(String basePackage,
			Class<? extends Annotation> annotationClass) {
		Set<Class<?>> classesWithAnnotation = new HashSet<>();

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));

		for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
			try {
				classesWithAnnotation.add(Class.forName(bd.getBeanClassName()));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		return classesWithAnnotation;
	}

	public static boolean isPortAvailable(int port) {
		try {
			ServerSocket serverSocket = ServerSocketFactory.getDefault()
					.createServerSocket(port, 1, InetAddress.getByName("localhost"));
			serverSocket.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
