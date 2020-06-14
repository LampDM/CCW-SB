package com.ccw.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import com.ccw.demo.model.CodeExecutionThread;

@Component
public class CompilerService {

	private static final String classOutputFolder = ".";

	private static Object runIt(String cname, String fname, Class[] params, Object[] input) {

		File file = new File(classOutputFolder);
		try {
			URL url = file.toURL(); // file:/classes/demo
			URL[] urls = new URL[] { url };
			ClassLoader loader = new URLClassLoader(urls);
			Class thisClass = loader.loadClass(cname);

			// Find declared method with specific name and parameters
			Method thisMethod = thisClass.getDeclaredMethod(fname, params);

			Object instance = thisClass.newInstance();

			Object ret = thisMethod.invoke(instance, input);

			return ret;

		} catch (MalformedURLException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {

		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {

			System.out.println("Line Number->" + diagnostic.getLineNumber());
			System.out.println("code->" + diagnostic.getCode());
			System.out.println("Message->" + diagnostic.getMessage(Locale.ENGLISH));
			System.out.println("Source->" + diagnostic.getSource());
			System.out.println(" ");
		}
	}

	public static class InMemoryJavaFileObject extends SimpleJavaFileObject {

		private String contents = null;

		public InMemoryJavaFileObject(String className, String contents) throws Exception {
			super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.contents = contents;
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return contents;
		}
	}

	private static JavaFileObject getJavaFileObject(String source, String classname) {
		JavaFileObject so = null;
		try {
			so = new InMemoryJavaFileObject(classname, source);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return so;
	}

	public static boolean jCompile(Iterable<? extends JavaFileObject> files) {
		boolean val;
		// get system compiler:
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		// for compilation diagnostic message processing on compilation WARNING/ERROR
		MyDiagnosticListener c = new MyDiagnosticListener();

		// note - getStandardFileManager needs JDK in the build path to work NOT JRE
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);

		// specify classes output folder
		Iterable options = Arrays.asList("-d", classOutputFolder); // note - class output folder must exist
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, files);
		val = task.call();
		return val;
	}

	// Made for optimising testing speed if we want many tests
	public static String testIt(String[] ex_outputs, String classname, String fname, Class[] params,
			ArrayList<Object[]> ex_inputs, long maxtime) {
		String result = "";
		File file = new File(classOutputFolder);
		int testc = 0;
		try {
			URL url = file.toURL(); // file:/classes/demo
			URL[] urls = new URL[] { url };
			ClassLoader loader = new URLClassLoader(urls);
			Class thisClass = loader.loadClass(classname);

			// Find declared method with specific name and parameters
			Method thisMethod = thisClass.getDeclaredMethod(fname, params);

			Object instance = thisClass.newInstance();
			
			
			// Main test loop
			for (int k = 0; k < ex_inputs.size(); k++) {
				String ex_ret = ex_outputs[k];
				
				
				// Run each input in a new thread and give it 'maxtime' for execution
				CodeExecutionThread cex = new CodeExecutionThread(thisMethod, instance, ex_inputs.get(k));
				cex.setName("Test "+(k+1)+" thread");
				cex.start();

				//TODO terminate thread safely somehow
//				Thread.sleep(maxtime);
//				cex.interrupt();
				cex.join(maxtime);
				
				Object ret = cex.getRet();
				
				if (ret == null) {
					result+= "testStopped;";
					continue;
				}

				if (ret.getClass().isArray()) {
					String element_type = ret.getClass().getComponentType().toString();

					// Array returns
					// TODO check misspellings
					// TODO create unit tests for all
					String[] ex_newarr = ex_ret.split(",");
					boolean testok = true;
					if (element_type.equals("byte")) {
						byte[] newarr = (byte[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != Byte.parseByte(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("short")) {
						short[] newarr = (short[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != Short.parseShort(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("int")) {
						int[] newarr = (int[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != Integer.parseInt(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("long")) {
						long[] newarr = (long[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != Long.parseLong(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("float")) {
						float[] newarr = (float[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != Float.parseFloat(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("double")) {
						double[] newarr = (double[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != Double.parseDouble(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("char")) {
						// Debugged OK
						char[] newarr = (char[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (newarr[i] != ex_newarr[i].charAt(1)) {
								testok = false;
							}
						}
					} else if (element_type.equals("class java.lang.String")) {
						// Debugged OK
						String[] newarr = (String[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (!newarr[i].equals(ex_newarr[i])) {
								testok = false;
							}
						}
					} else if (element_type.equals("boolean")) {
						// Debugged OK
						boolean[] newarr = (boolean[]) ret;
						for (int i = 0; i < newarr.length; i++) {
							if (!(newarr[i] == Boolean.parseBoolean(ex_newarr[i]))) {
								testok = false;
							}
						}
					}

					if (testok) {
						testc++;
					} else {
						result += String.format("Test %d fail;", k + 1);
						// System.out.println(String.format("Test %d fail", k + 1));
						// System.out.println(ex_ret);
						// TODO function to print arrays
					}

				} else {
					// Single value returns
					String retS = String.valueOf(ret);

					// TODO implement support for comparing sets and not just ordered lists
					if (retS.equals(ex_ret)) {
						testc++;

					} else {
						result += String.format("Test %d fail;", k + 1);
						// System.out.println(ex_ret);
						// System.out.println(ret);
					}
				}
			}

		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// System.out.println(String.format("%d/%d", testc, ex_outputs.length));
		result += String.format("%d/%d", testc, ex_outputs.length); 
		
		return result;
	}

	public CompilerService() {

	}

	public ArrayList<String> start(String tests, Long maxtime, String user_code) throws Exception {
		ArrayList<String> result = new ArrayList<String>();

		// TODO Pre-process student code
		// TODO handle package keywords
		// TODO what can student write
		// TODO function
		// TODO class
		JSONParser parser = new JSONParser();
		System.out.println(tests);
		Object object = parser.parse(tests);
		JSONObject jsonObject = (JSONObject) object;

		// TODO strip down user code
		// TODO add all sorts of used libraries
		String contents = String.format("package test1; import java.util.Arrays;  public class Dummy { %s } ",
				user_code);

		String cname = "test1.Dummy";
		// TODO make it even more compact
		JSONArray ja = (JSONArray) jsonObject.get("function");
		String fname = (String) ja.get(0);
		String rettype = (String) ja.get(1);

		// 1.Construct an in-memory java source file from your dynamic code
		JavaFileObject file = getJavaFileObject(contents, cname);
		Iterable<? extends JavaFileObject> files = Arrays.asList(file);

		// 2.Compile your files by JavaCompiler
		// CAPTURE OUTPUT OF JAVA COMPILER
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pstream = new PrintStream(baos);
		// IMPORTANT: Save the old System.out!
		PrintStream old = System.out;
		System.setOut(pstream);

		// TODO also make the text not dissappear on submit
		// TODO also add a delete all button

		// TODO add a new thread for this thing that stops it when the time is too high

		boolean comp_result = jCompile(files);

		if (comp_result) {
			result.add("ok");
			// Example of single testing a function
			// Object obj1 = runIt("test1.Dummy", "funarr", new Class[]{int[].class}, new
			// Object[]{new int[]{13, 3, 3}});
			// 3.Load your class by URLClassLoader, then instantiate the instance, and call
			// method by reflection
			// Function input - function "envelope" name - return type - parameter types in
			// a list
			JSONArray parr = (JSONArray) ja.get(2);

			// Parse classes of parameters
			Class params[] = get_class(new Class[parr.size()], parr);

			// Class of the function ret type
			JSONArray jarr = new JSONArray();

			jarr.add(rettype);

			// TODO decide if it's clever to use the actual types or just compare strings
			// Class rettypec[] = get_class(new Class[1], jarr);
			// tests parsing in the JSON
			JSONArray ta = (JSONArray) jsonObject.get("tests");

			ArrayList<Object[]> ex_inputs = new ArrayList<>();
			String ex_outputs[] = new String[ta.size()];

			Object paramsObj[];
			// For each test case
			for (int k = 0; k < ta.size(); k++) {
				paramsObj = new Object[params.length];
				JSONArray p = (JSONArray) ta.get(k);
				System.out.println(p);
				for (int j = 0; j < params.length; j++) {
					JSONArray ps = (JSONArray) p.get(0);
					System.out.println(ps.get(j));
					paramsObj[j] = get_cparam(params[j], (String) ps.get(j));
				}

				// Set expected inputs
				ex_inputs.add(paramsObj);

				// Set expected output
				ex_outputs[k] = (String) p.get(p.size() - 1);
			}

			String[] testit = testIt(ex_outputs, cname, fname, params, ex_inputs, maxtime).split(";");
			for (String s : testit) {
				result.add(s);
			}
		} else {
			// In case of error show this
			// Show what happened
			result.add(baos.toString());
		}

		// Put things back
		System.out.flush();
		System.setOut(old);

		return result;
	}

	// Helper functions
	public static int typestr_to_id(String type_str) {
		// Primitive types
		if (type_str.equals("byte")) {
			return 0;
		}
		if (type_str.equals("short")) {
			return 1;
		}
		if (type_str.equals("int")) {
			return 2;
		}
		if (type_str.equals("long")) {
			return 3;
		}
		if (type_str.equals("float")) {
			return 4;
		}
		if (type_str.equals("double")) {
			return 5;
		}
		if (type_str.equals("char")) {
			return 6;
		}
		if (type_str.equals("String")) {
			return 7;
		}
		if (type_str.equals("boolean")) {
			return 8;
		}

		// 1 dim arrays
		if (type_str.equals("byte[]")) {
			return 9;
		}
		if (type_str.equals("short[]")) {
			return 10;
		}
		if (type_str.equals("int[]")) {
			return 11;
		}
		if (type_str.equals("long[]")) {
			return 12;
		}
		if (type_str.equals("float[]")) {
			return 13;
		}
		if (type_str.equals("double[]")) {
			return 14;
		}
		if (type_str.equals("char[]")) {
			return 15;
		}
		if (type_str.equals("String[]")) {
			return 16;
		}
		if (type_str.equals("boolean[]")) {
			return 17;
		}
		// TODO other dimensions of lists

		return -1;
	}

	public static Object get_cparam(Class c, String param) {
		Object o = null;
		// Primitve types
		if (c.equals(byte.class)) {
			o = Byte.parseByte(param);
		} else if (c.equals(short.class)) {
			o = Short.parseShort(param);
		} else if (c.equals(int.class)) {
			o = Integer.parseInt(param);
		} else if (c.equals(long.class)) {
			o = Long.parseLong(param);
		} else if (c.equals(float.class)) {
			o = Float.parseFloat(param);
		} else if (c.equals(double.class)) {
			o = Double.parseDouble(param);
		} else if (c.equals(char.class)) {
			o = param.charAt(0);
		} else if (c.equals(String.class)) {
			o = param;
		} else if (c.equals(boolean.class)) {
			o = Boolean.valueOf(param);
		} // 1 dim arrays
		else if (c.equals(byte[].class)) {
			o = Boolean.valueOf(param);
		} else if (c.equals(short[].class)) {
			o = Boolean.valueOf(param);
		} else if (c.equals(int[].class)) {
			String[] parr = param.split(",");
			int[] a1 = new int[parr.length];
			for (int k = 0; k < a1.length; k++) {
				a1[k] = Integer.parseInt(parr[k]);
			}
			o = a1;
		} else if (c.equals(long[].class)) {
			String[] parr = param.split(",");
			long[] a1 = new long[parr.length];
			for (int k = 0; k < a1.length; k++) {
				a1[k] = Long.parseLong(parr[k]);
			}
			o = a1;
		} else if (c.equals(float[].class)) {
			String[] parr = param.split(",");
			float[] a1 = new float[parr.length];
			for (int k = 0; k < a1.length; k++) {
				a1[k] = Float.parseFloat(parr[k]);
			}
			o = a1;
		} else if (c.equals(double[].class)) {
			String[] parr = param.split(",");
			double[] a1 = new double[parr.length];
			for (int k = 0; k < a1.length; k++) {
				a1[k] = Double.parseDouble(parr[k]);
			}
			o = a1;
		} else if (c.equals(char[].class)) {
			String[] parr = param.split(",");
			char[] a1 = new char[parr.length];
			for (int k = 0; k < a1.length; k++) {
				a1[k] = parr[k].charAt(1);
			}
			o = a1;
		} else if (c.equals(String[].class)) {
			o = param.split(",");
		} else if (c.equals(boolean[].class)) {
			String[] parr = param.split(",");
			boolean[] a1 = new boolean[parr.length];
			for (int k = 0; k < a1.length; k++) {
				a1[k] = Boolean.parseBoolean(parr[k]);
			}
			o = a1;
		}
		return o;
	}

	private static Class[] get_class(Class[] aClass, JSONArray parr) {
		for (int k = 0; k < aClass.length; k++) {
			switch (typestr_to_id((String) parr.get(k))) {
			// Primitive types
			case 0:
				aClass[k] = byte.class;
				break;
			case 1:
				aClass[k] = short.class;
				break;
			case 2:
				aClass[k] = int.class;
				break;
			case 3:
				aClass[k] = long.class;
				break;
			case 4:
				aClass[k] = float.class;
				break;
			case 5:
				aClass[k] = double.class;
				break;
			case 6:
				aClass[k] = char.class;
				break;
			case 7:
				aClass[k] = String.class;
				break;
			case 8:
				aClass[k] = boolean.class;
				break;
			// 1 dim arrays
			case 9:
				aClass[k] = byte[].class;
				break;
			case 10:
				aClass[k] = short[].class;
				break;
			case 11:
				aClass[k] = int[].class;
				break;
			case 12:
				aClass[k] = long[].class;
				break;
			case 13:
				aClass[k] = float[].class;
				break;
			case 14:
				aClass[k] = double[].class;
				break;
			case 15:
				aClass[k] = char[].class;
				break;
			case 16:
				aClass[k] = String[].class;
				break;
			case 17:
				aClass[k] = boolean[].class;
				break;
			default:
				break;
			}
		}
		return aClass;
	}

}
