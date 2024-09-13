package com.pacemaker.global.util.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class Test {
	public static void main(String[] args) {
		// String json = "{ \"root\": { \"name\": \"root\", \"age\": \"30\", \"firstNode\": { \"name\": \"FirstNode\", \"depth\": \"1\", \"secondNode\": { \"name\": \"SecondeNode\", \"depth\": \"2\" }, \"secondNode2\": { \"name\": \"SecondeNode2\", \"depth\": \"2\" } } } }";
		// String json = "{ \"root\": { \"name\": \"\", \"age\": \"30\", \"firstNode\": { \"name\": \"FirstNode\", \"depth\": \"1\", \"secondNode\": { \"name\": \"SecondeNode\", \"depth\": \"2\" }, \"secondNode2\": { \"name\": \"SecondeNode2\", \"depth\": \"2\" } } } }";
		// String json = "{ \"root\": { \"name\": \"null\", \"age\": \"30\", \"firstNode\": { \"name\": \"FirstNode\", \"depth\": \"1\", \"secondNode\": { \"name\": \"SecondeNode\", \"depth\": \"2\" }, \"secondNode2\": { \"name\": \"SecondeNode2\", \"depth\": \"2\" } } } }";
		String json = "{ \"root\": { \"name\": null, \"age\": \"30\", \"firstNode\": { \"name\": \"FirstNode\", \"depth\": \"1\", \"secondNode\": { \"name\": \"SecondeNode\", \"depth\": \"2\" }, \"secondNode2\": { \"name\": \"SecondeNode2\", \"depth\": \"2\" } } } }";


		ObjectMapper mapper = new ObjectMapper();
		try {
			// Root root = mapper.readValue(json, Root.class);
			// System.out.println("Deserialization successful!");
			// RootContainer rootContainer = mapper.readValue(json, RootContainer.class);
			RootContainer rootContainer = new Gson().fromJson(json, RootContainer.class);
			System.out.println(rootContainer.root.name);
			System.out.println(rootContainer.root.name == null);

			System.out.println(rootContainer.root.age);
			System.out.println(rootContainer.root.firstNode.name);
			System.out.println(rootContainer.root.firstNode.depth);
			System.out.println(rootContainer.root.firstNode.secondNode.name);
			System.out.println(rootContainer.root.firstNode.secondNode.depth);
			System.out.println(rootContainer.root.firstNode.secondNode2.name);
			System.out.println(rootContainer.root.firstNode.secondNode2.depth);
			System.out.println("----update----");
			rootContainer.root.firstNode.secondNode.name = "update";
			rootContainer.root.firstNode.secondNode.depth = 444;
			System.out.println(rootContainer.root.firstNode.secondNode.name);
			System.out.println(rootContainer.root.firstNode.secondNode.depth);

			String tt = new Gson().toJson(rootContainer);
			System.out.println(tt);

			System.out.println("-------------------------");
			RootContainer r = new Gson().fromJson(tt, RootContainer.class);
			System.out.println(r.root.name);
			System.out.println(r.root.name == null);
			System.out.println(r.root.age);
			System.out.println(r.root.firstNode.name);
			System.out.println(r.root.firstNode.depth);
			System.out.println(r.root.firstNode.secondNode.name);
			System.out.println(r.root.firstNode.secondNode.depth);
			System.out.println(r.root.firstNode.secondNode2.name);
			System.out.println(r.root.firstNode.secondNode2.depth);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
