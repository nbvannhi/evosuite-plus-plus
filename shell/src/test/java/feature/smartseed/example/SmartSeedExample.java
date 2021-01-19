package feature.smartseed.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;



public class SmartSeedExample {
	String[] lesSuffixes;
	String laDescription;
	private MenuItemList rootList;
    private ArrayList orphans;
    
    private HSSFWorkbook workbook;
	private TemplateWorkbook tWorkbook;
	private TemplateSheet tSheet;
	
	public void dynamicExample1(int x, int y) {
		double z = Math.floor((y+999999)/1333);
		if(x==z) {
			System.currentTimeMillis();
		}
	}
	
	public void dynamicExample2(int x, int y) {
		if(x/20000==345) {
			System.currentTimeMillis();
		}
	}
	
	public void staticExample1(int x, int y) {
		if(x==34500000) {
			System.currentTimeMillis();
		}
	}
	
	public void staticExample2(String x, int y) {
		if(x.equals("34500000")) {
			System.currentTimeMillis();
		}
	}
	
	public void staticExample3(String x, int y) {
		String a = x.substring(0, x.length()-3);
		if(a.equals("it is a difficult string")) {
			System.currentTimeMillis();
		}
	}
	
	//8
	public boolean accept(File f) {
		String suffixe = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if((i > 0) && (i < s.length() - 1)) {
			suffixe = s.substring(i + 1).toLowerCase();
		}
		return (suffixe != null) 
				&& (appartient(suffixe));
	}
	
	public boolean appartient(String suffixe) {
		for(int i = 0;i < lesSuffixes.length;i++) {
			if(suffixe.equals(lesSuffixes[i])) {
				return true;
			}
		}
		return false;
	}
	
	public void MonFilter(String[] lesSuffixes,String laDescription) {
		this.lesSuffixes = lesSuffixes;
		this.laDescription = laDescription;
	}
	
	//72 addMenuItem
	public void addMenuItem(IMenuItem item, String name)
	  {
	    if (item == null) {      return;    }
	     item.setName(name);
	    if (item.getParent() == null)
	     {
	       addMenuItemToList(item, rootList);
	     }
	    else
	    {
	       MenuItemList parentlist = findParentList(item, rootList);
	      if (parentlist == null) {
	
	         orphans.add(item);
	       } else {
	         addMenuItemToList(item, parentlist);
	       }
	    }
	  }
	 private MenuItemList findParentList(IMenuItem item, MenuItemList parentList)
	    {
	     if (item.getParent()== null) {
	       return null;  
	      }
	     if ((parentList.getMenuItem() != null) && (parentList.getMenuItem().getContained().equals(item.getParent())))
	      {
	       parentList.getMenuItem().setLeaf(false);
	       return parentList;	  
	      }
	     Iterator children = parentList.getChildrenIterator();
	     while (children.hasNext())    {
	       MenuItemList childList = (MenuItemList)children.next();
	       MenuItemList found = findParentList(item, childList);
	       if (found != null) {
	         return found;	  
	        }
	      }
	     return null;
	    }
	 private void addMenuItemToList(IMenuItem item, MenuItemList parentlist)
	  {
	    item.setIndex(parentlist.getChildrenSize());
	    MenuItemList menulist = new MenuItemList(item);
	    
	    attachOrphans(menulist);
	    parentlist.addChild(menulist);
	   }
	 private void attachOrphans(MenuItemList menulist)
	   {
	    for (int i = 0; i < orphans.size(); i++)    {
	    	IMenuItem current = (IMenuItem)orphans.get(i);
	    	MenuItemList list = new MenuItemList(current);
	    	orphans.remove(i);
	    	if ((current.getParent() != null) && (current.getParent().equals(menulist.getMenuItem().getContained()))) {
	        menulist.addChild(list);
	        } else {
	         rootList.addChild(list);
	         }
	    	attachOrphans(list);
	    	}
	    }
	 //3 addResource
	 
	 //5 parse
	 public TemplateWorkbook parse(Set<String> excludedSheetNames)
		{
			int nSheets = workbook.getNumberOfSheets();
			for (int i = 0; i < nSheets; i++)
			{
				HSSFSheet sheet = workbook.getSheetAt(i);
				String sheetName = workbook.getSheetName(i);
				if (excludedSheetNames!=null && 
						!excludedSheetNames.contains(sheetName))
				{
					System.currentTimeMillis();
				}
			}
			return tWorkbook;
		}
	 
	 //26 loadInstructions
	 public static boolean loadInstructions(String paramString)
	 {
	 	int i = 0;
	 	if (paramString.isEmpty()) {
	 		System.currentTimeMillis();
	 		}
	 	if (paramString.equalsIgnoreCase("q")) {
	 		System.exit(0);
	 		}
	 	return true;
	 	}
}
