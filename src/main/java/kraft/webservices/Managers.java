package kraft.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import kasper.kernel.Home;
import kasper.kernel.manager.Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/managers")
public class Managers {
	private final Gson gson = createGson();

	private static Gson createGson() {
		return new GsonBuilder()//
				.setPrettyPrinting()//
				.create();
	}
 
	@GET
	@Produces("application/json")
	public String getAllManagers() {
		final Collection<? extends Manager> managers = Home.getContainer().getAllManagers();
		final List<MyManager> myManagers = new ArrayList<MyManager>();
		for (final Manager manager : managers) {
			myManagers.add(new MyManager(manager));
		}
		return gson.toJson(myManagers);
	} 

	public static final class MyManager {
		final String name;

		MyManager(Manager manager) {
			name = manager.getClass().getSimpleName();
		}
	}
}
