package com.bnhp.service;

import io.confluent.kafka.schemaregistry.json.JsonSchema;
import io.confluent.kafka.schemaregistry.json.JsonSchemaUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SchemasRegistryCreatorService {

	AtomicInteger counter = new AtomicInteger(1);
	public JsonSchema createSchemaFromInnerClassJson(String json) {
		JsonSchema sc = null;
		ClassNode cn = new ClassNode();
		cn.version = Opcodes.V1_5;
		cn.access = Opcodes.ACC_PUBLIC;

		cn.name = "com/test/MyClass";
		cn.superName = "java/lang/Object";
		ClassWriter cw = new ClassWriter(0);
		JSONObject jo = new JSONObject(json);
		for (Iterator<String> it = jo.keys(); it.hasNext(); ) {
			String key = it.next();
			Object o = jo.get(key);
			if (o instanceof Short) {
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "S", null, 100));
			}
			else if (o instanceof Double || o instanceof BigDecimal) {
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "D", null, 100));
			}
			else if (o instanceof Long) {
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "J", null, 100));
			}
			else if (o instanceof Integer) {
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "I", null, 100));
			} else if (o instanceof Boolean) {
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "Z", null, Boolean.TRUE));
			} else if (o instanceof String) {
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "Ljava/lang/String;", null, "hello"));
			} else if (o instanceof JSONArray) {
				String c = guessArrayType((JSONArray) o);
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "[Ljava/lang/" + c + ";",  null, null));
			} else if (o instanceof JSONObject) {
				String className = createInnerClass((JSONObject) o, key).replace('.', '/');
				cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "Lcom/test/" + className + ";", null, ""));
			} else {
				System.out.println(",type: ?, value:" +o.toString());
			}
		}

		MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);

		InsnList insnConstructor = new InsnList();
		insnConstructor.add(new IntInsnNode(Opcodes.ALOAD, 0));
		insnConstructor.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
		insnConstructor.add(new InsnNode(Opcodes.RETURN));
		mn.instructions.add(insnConstructor);
		mn.maxStack = 2;
		mn.maxLocals = 2;
		cn.methods.add(mn);

		cn.accept(cw);
		byte[] b = cw.toByteArray();

		MyClassLoader mcl = new MyClassLoader();

		Class c = mcl.defineClass(cn.name.replace('/', '.'), b);
		Object o = null;
		try {
			o = c.newInstance();
			sc = JsonSchemaUtils.getSchema(o);
			System.out.println("schema --> " + JsonSchemaUtils.getSchema(o));

		} catch (InstantiationException | IllegalAccessException | SecurityException | IOException e) {
			e.printStackTrace();
		}
		return sc;
	}

	@NotNull
	private String createInnerClass(JSONObject jo, String className) {
		ClassNode cn2 = new ClassNode();
		cn2.version = Opcodes.V1_5;
		cn2.access = Opcodes.ACC_PUBLIC;
		counter.incrementAndGet();
		cn2.name = "com/test/" + counter + "/" + className;
		cn2.superName = "java/lang/Object";
		ClassWriter cw2 = new ClassWriter(0);
		for (Iterator<String> it = jo.keys(); it.hasNext(); ) {
			String key = it.next();
			Object o = jo.get(key);
			if (o instanceof Double || o instanceof BigDecimal) {
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "D", null, 100));
			}
			else if (o instanceof Short) {
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "S", null, 100));
			}
			else if (o instanceof Long) {
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "J", null, 100));
			}
			else if (o instanceof Integer) {
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "I", null, 100));
			} else if (o instanceof Boolean) {
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "Z", null, Boolean.TRUE));
			} else if (o instanceof String) {
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "Ljava/lang/String;", null, "hello"));
			} else if (o instanceof JSONArray) {
				String c = guessArrayType((JSONArray) o);
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "[Ljava/lang/" + c + ";",  null, null));
			} else if (o instanceof JSONObject) {
				String nameOfClass = createInnerClass((JSONObject) o, key).replace('.', '/');
				cn2.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, key, "Lcom/test/" + nameOfClass + ";", null, ""));
			} else {
				System.out.println("type: ?, value:" +o.toString());
			}
		}


		MethodNode mn2 = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);

		InsnList insnConstructor2 = new InsnList();
		insnConstructor2.add(new IntInsnNode(Opcodes.ALOAD, 0));
		insnConstructor2.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
		insnConstructor2.add(new InsnNode(Opcodes.RETURN));
		mn2.instructions.add(insnConstructor2);
		mn2.maxStack = 20;
		mn2.maxLocals = 20;
		cn2.methods.add(mn2);
		cn2.accept(cw2);
		byte[] b2 = cw2.toByteArray();
		loadClass(b2,  counter + "." + className);
//        MyClassLoader mcl2 = new MyClassLoader();
//        Class c2 = mcl2.defineClass(cn2.name.replace('/', '.'), b2);
		return counter + "/" + className;
	}

	class MyClassLoader extends ClassLoader {
		public Class defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
	}

	private String guessArrayType(JSONArray ja) {
		if (ja == null || ja.length() == 0) {
			String[] classNameTmp = Object.class.getName().split("\\.");
			int size = classNameTmp.length;
			return classNameTmp[size-1];
		}

		Class cl = ja.get(0).getClass();
		if (cl.equals(JSONArray.class) || cl.equals(JSONObject.class)) {
			String[] classNameTmp = Object.class.getName().split("\\.");
			int size = classNameTmp.length;
			return classNameTmp[size-1];
		}

		for (int i = 1; i < ja.length(); i++) {
			if (!cl.equals(ja.get(i).getClass())) {
				cl = Object.class;
				break;
			}
		}

		String[] classNameTmp = cl.getName().split("\\.");
		int size = classNameTmp.length;
		return classNameTmp[size-1];
	}

	private Class loadClass(byte[] b, String className) {
		//check if class exists
		try {
			Class x = Class.forName("com.test." + className);
			return null;
		} catch (LinkageError | ClassNotFoundException e) {
			System.out.println(" =========== create class:  " + className + " was started =============");
		}
		// Override defineClass (as it is protected) and define the class.
		Class clazz = null;
		try {
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			Class cls = Class.forName("java.lang.ClassLoader");
			java.lang.reflect.Method method =
					cls.getDeclaredMethod(
							"defineClass",
							new Class[]{String.class, byte[].class, int.class, int.class});

			// Protected method invocation.
			method.setAccessible(true);
			try {
				Object[] args =
						new Object[]{"com.test." + className, b, new Integer(0), new Integer(b.length)};
				clazz = (Class) method.invoke(loader, args);
			} finally {
				method.setAccessible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}
}