package churndb.tasks;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class Runner {

	private static Map<String, Map<String, Method>> tasks;

	private static Help help = new Help();

	private static String prefix = "mvn exec:java ";

	public static void main(String... args) {
		setupTasks();

		if (isShowHelp(args)) {
			showHelp();
			return;
		}

		run(args);

	}

	private static void run(String... args) {

		Method taskMethod = getTaskMethod(args[0]);

		
		

	}

	private static Method getTaskMethod(String taskCmd) {
		if (StringUtils.countMatches(taskCmd, ":") != 1) {
			help.println("command {0} is not in the right format task:method", taskCmd);
			fail();
		}

		String[] split = taskCmd.split(":");
		String taskKey = split[0];
		String methodKey = split[1];

		if (!tasks.containsKey(taskKey)) {
			help.println("task {0} does not exist", taskKey);
			fail();
		}

		Map<String, Method> taskMethods = tasks.get(taskKey);

		if (!taskMethods.containsKey(methodKey)) {
			help.println("task {0} does not contain method {1}", taskKey, methodKey);
			fail();
		}

		return taskMethods.get(methodKey);
	}

	private static void fail() {
		System.exit(-1);
	}

	private static boolean isShowHelp(String... args) {
		return args.length == 0 || args[0].equalsIgnoreCase("-h");
	}

	private static void setupTasks() {
		tasks = new TreeMap<String, Map<String, Method>>();

		addTask(ApplicationTask.class);
		addTask(ProjectTask.class);
	}

	private static void addTask(Class<? extends Task> task) {
		Map<String, Method> taskMethods = new TreeMap<String, Method>();

		for (Method method : task.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}

			taskMethods.put(getTaskMethodKey(method), method);
		}

		tasks.put(getTaskKey(task), taskMethods);
	}

	public static void showHelp() {
		for (String taskKey : tasks.keySet()) {
			listTask(taskKey);
		}
	}

	private static void listTask(String taskKey) {
		Map<String, Method> taskMethods = tasks.get(taskKey);

		for (String methodKey : taskMethods.keySet()) {
			Method method = taskMethods.get(methodKey);

			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}

			help.println(prefix + taskKey + ":" + methodKey + getTaskMethodHelp(method));
		}
	}

	private static String getTaskMethodHelp(Method method) {
		RunnerHelp runnerHelp = method.getAnnotation(RunnerHelp.class);

		if (runnerHelp == null || runnerHelp.value().equals("")) {
			return "";
		}

		return " " + runnerHelp.value();
	}

	private static String getTaskMethodKey(Method method) {
		return StringUtils.uncapitalize(method.getName());
	}

	private static String getTaskKey(Class<? extends Task> task) {
		return StringUtils.uncapitalize(task.getSimpleName().replaceAll("Task", ""));
	}
}
