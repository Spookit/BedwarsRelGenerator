package thito.breadcore.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Paginator<T> {

	public static void main(String[]args) {
		Paginator<Integer> ints = new Paginator<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),4,1);
		System.out.println(ints.getPages());
		System.out.println(ints.isValidPage(17));
	}
	private final List<T> l;
	private final int itemPerPage;
	private final int steps;
	public Paginator(List<T> list,int itemPerPage) {
		this(list,itemPerPage,itemPerPage);
	}
	public Paginator(List<T> list,int itemPerPage,int steps) {
		if (itemPerPage <= 0) throw new IllegalArgumentException("itemPerPage");
		if (steps <= 0) throw new IllegalArgumentException("steps");
		l = list;
		this.itemPerPage = itemPerPage;
		this.steps = steps;
	}
	public int getSteps() {
		return steps;
	}
	public int getItemPerPage() {
		return itemPerPage;
	}
	public List<T> getAll() {
		return l;
	}
	public Map<Integer,List<T>> getPages() {
		Map<Integer,List<T>> lists = new HashMap<>();
		int pageIndex = 0;
		for (int i = 0; i < l.size(); i+=steps) {
			lists.put(pageIndex, getPage(pageIndex));
			pageIndex++;
		}
		return lists;
	}
	public boolean isValidPage(int page) {
		return getPage(page).size() == itemPerPage;
	}
	public List<T> getPage(int page) {
		return l.subList(
				Util.safeRange(l.size(), steps * page, 0), 
				Util.safeRange(l.size(), (steps * page) + itemPerPage, 0));
	}
	
}
