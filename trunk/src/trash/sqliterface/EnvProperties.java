// Some Environment Properties
public class EnvProperties {
	public static void main(String args[]) {
		System.out.println("ENVIRONMENT PROPERTIES");
		System.out.println("Architecturer: " + System.getProperty("os.arch"));
		System.out.println("Operating System: " + System.getProperty("os.name"));
		System.out.println("Operating Sys Ver: "+ System.getProperty("os.version"));
		System.out.println("User Directory: " + System.getProperty("user.dir"));
		System.out.println("User Home Dir: " +System.getProperty("user.home"));
		System.out.println("User Name: " + System.getProperty("user.name"));
	}
}
