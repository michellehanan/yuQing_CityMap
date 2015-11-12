import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CountryCityMap {
	Map<String,List<Node>> map=new HashMap<String,List<Node>>();//store <name,node location>
	Map<Integer,String> promap = new HashMap<Integer,String>();// store<proID, proName>
	Map<Integer,String> citymap = new HashMap<Integer,String>();// store<cityID, cityName>

	public void FiletoMap() throws IOException{
		String line;
		InputStream in = new FileInputStream("reference/location.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"GBK"));
		//PrintWriter out = new PrintWriter("result/final_o.txt");
		
		while((line=br.readLine()) != null){
			//country=中国
		if (line.equals("INSERT S_Nation (NationID, NationName) VALUES (1, '中国' );"))
			 {
			Node root = new Node();
			root.id=0;
			root.name="中国";
			root.parent=null;
			root.cityid=0;
			root.proid=0;
			root.distid=0;
			//root.children=null;
			List<Node> list= new ArrayList<Node>();
			list.add(root);
			map.put("中国", list);
			 }
	
		else if(line.startsWith("INSERT S_Province (ProvinceID, ProvinceName,NationID) VALUES (")){
			//province
				String prostr="INSERT S_Province (ProvinceID, ProvinceName,NationID) VALUES (";
				int prolen= prostr.length();//=place of provinceID
				//(1, '北京市' ,1);
				String proname;
				int head = line.indexOf("'");
				int tail = line.lastIndexOf("'");
				int proID;
				//1-9
				if(head-prolen==3){
					proID=line.charAt(prolen)-48;//province ID
				}//if1-9
				//10-34
				else
				{
					String proID2=line.substring(prolen, head-2);
					proID=Integer.parseInt(proID2);//province ID
				}//else10-34		
				Node pronode = new Node();
				pronode.id=proID;
				pronode.cityid=0;
				pronode.distid=0;
				pronode.proid=proID;
				pronode.name=line.substring(head+1, tail) ;
				promap.put(pronode.id,pronode.name);
				pronode.parent=map.get("中国").get(0);
			    map.get("中国").get(0).children.add(pronode);
			    if(map.containsKey(pronode.name)){
			    	map.get(pronode.name).add(pronode);
			    }
			    else{
				List<Node> list= new ArrayList<Node>();
				list.add(pronode);
				map.put(pronode.name, list);
			    }
			}//else if province
		
		else if(line.startsWith("INSERT S_City (CityID, CityName, ZipCode, ProvinceID) VALUES (")){
			//city
				String citystr="INSERT S_City (CityID, CityName, ZipCode, ProvinceID) VALUES (";
				int citylen= citystr.length();//=place of cityID
				//(1, '北京市', '100000', 1)
				String proname;
				int head = line.indexOf("'");
				int tail = line.indexOf("', '");
				int tail2 = line.lastIndexOf(" ");
				int cityID;
				//1-9
				if(head-citylen==3){
					cityID=line.charAt(citylen)-48;//province ID
				}//if1-9
				//10-345
				else
				{
					String cityID2=line.substring(citylen, head-2);
					cityID=Integer.parseInt(cityID2);//province ID
				}//else10-34		
				String proID2= line.substring(tail2+1,line.length()-2);
				int proID=Integer.parseInt(proID2);
				
				Node citynode = new Node();
				citynode.id=cityID;
				citynode.cityid=cityID;
				citynode.distid=0;
				citynode.proid=proID;
				citynode.name=line.substring(head+1, tail);
				citymap.put(citynode.id,citynode.name);
				int n=0;	
				while(map.get(promap.get(proID)).get(n).proid != proID){
					n++;
				}
				citynode.parent=map.get(promap.get(citynode.proid)).get(n);
			    map.get(citynode.parent.name).get(n).children.add(citynode);
			    if(map.containsKey(citynode.name)){
			    	map.get(citynode.name).add(citynode);
			    }
			    else{
				List<Node> list= new ArrayList<Node>();
				list.add(citynode);
				map.put(citynode.name, list);
			    }
			}//else if city
		
		else if(line.startsWith("INSERT S_District (DistrictID, DistrictName, CityID) VALUES (")){
			//district
				String diststr="INSERT S_District (DistrictID, DistrictName, CityID) VALUES (";
				int distlen= diststr.length();//=place of distID
				//(1, '东城区', 1)
				String cityname;
				int head = line.indexOf("'");
				int tail = line.lastIndexOf("'");
				int distID;
				//1-9
				if(head-distlen==3){
					distID=line.charAt(distlen)-48;//province ID
				}//if1-9
				//10-2862
				else
				{
					String distID2=line.substring(distlen, head-2);
					distID=Integer.parseInt(distID2);//province ID
				}//else10-2862	
				
				String cityID2= line.substring(tail+3,line.length()-2);
				int cityID=Integer.parseInt(cityID2);
				
				Node distnode = new Node();
				distnode.id=distID;
				distnode.cityid=cityID;
				distnode.distid=distID;
				
				int n=0;
				while(map.get(citymap.get(cityID)).get(n).cityid != cityID){
					n++;
				}
				distnode.proid=map.get(citymap.get(cityID)).get(n).parent.proid;
				distnode.name=line.substring(head+1, tail);	
				int m=0;	
				while(map.get(citymap.get(cityID)).get(m).cityid != cityID){
					m++;
				}
				distnode.parent=map.get(citymap.get(distnode.cityid)).get(m);
			    map.get(distnode.parent.name).get(m).children.add(distnode);	
			    if(map.containsKey(distnode.name)){
			    	map.get(distnode.name).add(distnode);
			    }
			    else{
				List<Node> list= new ArrayList<Node>();
				list.add(distnode);
				map.put(distnode.name, list);
			    }
			}//else if district
		}//while
	}//FiletoMap
	
	public List<String> SearchParent(String name,List<String> plist){
		//String name="海伦市";
		int n=0;
		if(name.equals("北京市" )||name.equals("天津市" )||name.equals("上海市" )||name.equals("重庆市" )){
			while(map.get(name).get(n).id !=0){
				plist.add(map.get(name).get(n).parent.name);
				name = map.get(name).get(n).parent.name;
			}
		}
		else{
			for(n=0;n<map.get(name).size();n++){
				while(map.get(name).get(n).id !=0){
					plist.add(map.get(name).get(n).parent.name);
					name = map.get(name).get(n).parent.name;
				}
			}
		}
		return plist;
	}
	
	public List<String>  SearchChild(String name,Integer level,List<String> clist){
		int n=0;
		if(map.get(name).get(n).children==null||map.get(name).get(n).children.size()==0)
			return clist;
		if(name.equals("北京市" )||name.equals("天津市" )||name.equals("上海市" )||name.equals("重庆市" )){
			n=1;
			int childnum=map.get(name).get(n).children.size();
			if(level>0){
				level--;
				for(int i=0; i<childnum; i++){
					clist.add(map.get(name).get(n).children.get(i).name);
					SearchChild(map.get(name).get(n).children.get(i).name,level,clist);
				}
			}
			else
				return clist;
		}
		else{
			for(n=0;n<map.get(name).size();n++){
				int childnum=map.get(name).get(n).children.size();
				System.out.println(childnum);
				if(level>0){
					level--;
					for(int i=0; i<childnum; i++){
						clist.add(map.get(name).get(n).children.get(i).name);
						SearchChild(map.get(name).get(n).children.get(i).name,level,clist);
					}
				}
				else
					return clist;
			}
		}	
		return clist;
	}
	
	public void  SearchChild2(String name){
		int n=0;
		int level=1;
		for(Iterator<Node> it= map.get(name).get(n).children.iterator();it.hasNext();){
			for(int i=0; i<level;i++){
				name=it.next().name;
				System.out.println(name);
			}
		}
	}
	 
	public void listprint(List<String> list){
		for(int i=0;i<list.size();i++)
			System.out.println(list.get(i));
	}
	
	public static void main(String[] args) throws IOException, Exception {
		long startTime=System.currentTimeMillis();   
		
		CountryCityMap hmq= new CountryCityMap();
		List<String> childlist= new ArrayList<String>();
		List<String> childlist_out= new ArrayList<String>();
		List<String> parentlist= new ArrayList<String>();
		List<String> parentlist_out= new ArrayList<String>();
		hmq.FiletoMap();
		parentlist_out=hmq.SearchParent("海伦市",parentlist);
		childlist_out=hmq.SearchChild("上海市",2,childlist);
		System.out.println("The parents are:");
		hmq.listprint(parentlist);
		System.out.println("The children are:");
		hmq.listprint(childlist);
		//hmq.SearchChild2("杭州市");
		long endTime=System.currentTimeMillis();
		System.out.println("运行时间"+(endTime-startTime)+"ms");
	}
}

class Node {
    String name;
    int cityid;
    int proid;
    int distid;
    int id;
    Node parent;
    List<Node> children = new ArrayList<Node>();
}