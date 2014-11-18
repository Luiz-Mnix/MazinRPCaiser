package br.com.mnix.mazinrpcaiser.sample.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by mnix05 on 11/18/14.
 *
 * @author mnix05
 */
public interface ISample extends Serializable {
	int add(Integer n1, Integer n2);
	String concat(String s1, String s2);
	String concat(String s, Integer n);
	int[] repeat(Integer n);
	List<String> repeat(String s);
	Map<String, Integer> repeat(String s, Integer n);
	SubSample createSubSample(String s);
	void throwException(Boolean throwz);
}
