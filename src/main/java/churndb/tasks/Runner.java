package churndb.tasks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Runner {

	private static String prefix = "mvn exec:java ";

	private Map<String, Class<? extends Task>> tasks;

	private Map<String, Map<String, Method>> tasksMethods;

	private Help help = new Help();

	private Task task;

	private Method method;

	private List<Object> parameters;

	public static void main(String... args) {
		Runner runner = new Runner();
		runner.run(args);
	}

	private void run(String... args) {
		setup();

		if (isShowHelp(args)) {
			showHelp();
			fail();
		}

		if (initArgs(args)) {
			exec(args);
		}
	}

	private boolean initArgs(String... args) {
		String taskCmd = args[0];

		if (StringUtils.countMatches(taskCmd, ":") != 1) {
			help.println("command {0} is not in the right format task:method", taskCmd);
			return false;
		}

		String[] split = taskCmd.split(":");
		String taskKey = split[0];
		String methodKey = split[1];

		if (!initTask(taskKey)) {
			return false;
		}

		if (!initMethod(taskKey, methodKey)) {
			return false;
		}

		if (!initParameters(ArrayUtils.remove(args, 0))) {
			return false;
		}

		return true;
	}

	private boolean initTask(String taskKey) {
		if (!tasks.containsKey(taskKey)) {
			help.println("task {0} does not exist", taskKey);
			return false;
		}

		task = getTask(taskKey);
		return true;
	}

	private Task getTask(String taskKey) {
		try {
			return tasks.get(taskKey).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void exec(String... args) {
		try {
			method.invoke(task, parameters.toArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean initParameters(String[] parametersAsString) {

		if (method.getParameterTypes().length != parametersAsString.length) {
			help.println("wrong number of parameters for method {0}", getTaskMethodKey(method));
			return false;
		}

		parameters = new ArrayList<Object>();

		Class<?>[] parameterTypes = method.getParameterTypes();

		for (int i = 0; i < parametersAsString.length; i++) {
			parameters.add(getMethodParameter(parametersAsString[i], parameterTypes[i]));
		}

		return true;
	}

	private Object getMethodParameter(String parameterAsString, Class<?> parameterType) {
		try {
			Constructor<?> constructor = parameterType.getConstructor(String.class);
			return constructor.newInstance(parameterAsString);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean initMethod(String taskKey, String methodKey) {
		Map<String, Method> taskMethods = tasksMethods.get(taskKey);

		if (!taskMethods.containsKey(methodKey)) {
			help.println("task {0} does not contain method {1}", taskKey, methodKey);
			return false;
		}

		method = taskMethods.get(methodKey);
		return true;
	}

	private void fail() {
		System.exit(-1);
	}

	private boolean isShowHelp(String... args) {
		return args.length == 0 || args[0].equalsIgnoreCase("-h");
	}

	private void setup() {
		tasks = new TreeMap<String, Class<? extends Task>>();
		tasksMethods = new TreeMap<String, Map<String, Method>>();

		addTask(ApplicationTask.class);
		addTask(ProjectTask.class);
	}

	private void addTask(Class<? extends Task> task) {
		tasks.put(getTaskKey(task), task);

		Map<String, Method> taskMethods = new TreeMap<String, Method>();

		for (Method method : task.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}

			taskMethods.put(getTaskMethodKey(method), method);
		}

		tasksMethods.put(getTaskKey(task), taskMethods);
	}

	private void showHelp() {
		for (String taskKey : tasksMethods.keySet()) {
			listTask(taskKey);
		}
	}

	private void listTask(String taskKey) {
		Map<String, Method> taskMethods = tasksMethods.get(taskKey);

		for (String methodKey : taskMethods.keySet()) {
			Method method = taskMethods.get(methodKey);

			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}

			help.println(prefix + taskKey + ":" + methodKey + getTaskMethodHelp(method));
		}
	}

	private String getTaskMethodHelp(Method method) {
		RunnerHelp runnerHelp = method.getAnnotation(RunnerHelp.class);

		if (runnerHelp == null || runnerHelp.value().equals("")) {
			return "";
		}

		return " " + runnerHelp.value();
	}

	private String getTaskMethodKey(Method method) {
		return StringUtils.uncapitalize(method.getName());
	}

	private String getTaskKey(Class<? extends Task> task) {
		return StringUtils.uncapitalize(task.getSimpleName().replaceAll("Task", ""));
	}
}
