package br.com.mnix.mazinrpcaiser.sample.interfaces;

import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mnix05 on 11/18/14.
 *
 * @author mnix05
 */
@DefaultImplementation
public class DefaultSample implements ISample {
	private static final long serialVersionUID = 7396288970799813333L;

	@Override
	public int add(Integer n1, Integer n2) {
		return n1 + n2;
	}

	@Override
	public String concat(String s1, String s2) {
		return s1 + s2;
	}

	@Override
	public String concat(String s, Integer n) {
		return s + n;
	}

	@Override
	public int[] repeat(Integer n) {
		return new int[]{n, n, n};
	}

	@Override
	public List<String> repeat(String s) {
		List<String> list = new ArrayList<>();
		list.add(s);
		list.add(s);
		list.add(s);
		return list;
	}

	@Override
	public Map<String, Integer> repeat(String s, Integer n) {
		Map<String, Integer> map = new HashMap<>();
		map.put(s, n);
		return map;
	}

	@Override
	public ISubSample createSubSample(String s) {
		return new SubSample(s);
	}

	@Override
	public void throwException(Boolean throwz) {
		if(throwz) {
			throw new IllegalArgumentException("Throwing exception as requested");
		}
	}
}
