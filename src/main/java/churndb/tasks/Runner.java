package churndb.tasks;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Runner {

	private static List<Class<? extends ChurnDBTask>> tasks;

	private static PrintWriter pw = new PrintWriter(System.out);

	private static String prefix = "mvn exec:java ";

	public static void main(String... args) {
		setupTasks();
		listTasks();

		pw.close();
	}

	private static void setupTasks() {
		tasks = new ArrayList<Class<? extends ChurnDBTask>>();

		tasks.add(ApplicationTask.class);
		tasks.add(ProjectTask.class);
	}

	public static void listTasks() {
		for (Class<? extends ChurnDBTask> task : tasks) {
			listTask(task);
		}
	}

	private static void listTask(Class<? extends ChurnDBTask> task) {
		for (Method method : task.getDeclaredMethods()) {
			if(!Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			
			pw.println(prefix + getTaskName(task) + ":" + getTaskMethodName(method));
		}
	}

	private static String getTaskMethodName(Method method) {
		return StringUtils.uncapitalize(method.getName());
	}

	private static String getTaskName(Class<? extends ChurnDBTask> task) {
		return StringUtils.uncapitalize(task.getSimpleName().replaceAll("Task", ""));
	}
}
