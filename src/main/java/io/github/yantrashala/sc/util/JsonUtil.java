package io.github.yantrashala.sc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

public interface JsonUtil {

	static JsonNode getBlankJson() {
		return TextNode.valueOf("");
	}

}
