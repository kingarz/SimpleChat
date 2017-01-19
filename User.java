import java.io.PrintWriter;
import java.io.Serializable;

public class User implements Serializable {
	public String getName() {
		return name;
	}
	
	public String name;
	transient public PrintWriter out;
	User(String name,PrintWriter out)
	{
		this.name = name;
		this.out = out;
	}
	User(PrintWriter out)
	{
		this.out = out;
	}
	

}
