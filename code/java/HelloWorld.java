import java.util.HashMap;
public class HelloWorld
{
    public static void main(String[] args)
    {
      System.out.println("Hello World!");
      HashMap<String,String> hashMap = new HashMap<>();
      hashMap.put("a","1");
      System.out.println(hashMap.get("a"));
    }
}