package kraft.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import kasper.component.ComponentManager;
import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.DtField;
import kasper.domain.model.DtList;
import kasper.domain.model.DtObject;
import kasper.domain.util.DtObjectUtil;
import kasper.kernel.Home;
import kasper.kernel.di.injector.Injector;
import kasper.kernel.exception.KUserException;
import kasper.kernel.metamodel.URI;
import kraft.services.DataServices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/datas")
public class Datas {
	private static Injector injector = new Injector();

	@Inject
	private DataServices dataServices;

	public Datas() {
		injector.injectMembers(this, Home.getContainer().getManager(ComponentManager.class).getContainer());
	}

	private final Gson gson = createGson();

	private static Gson createGson() {
		return new GsonBuilder().registerTypeHierarchyAdapter(DtObject.class, new DtObjectConverter())//
				.setPrettyPrinting()//
				.create();
	}

	@GET
	@Path("/{dtDefinitionName}/{id}")
	@Produces("application/json")
	public String getObject(@PathParam("dtDefinitionName") final String dtDefinitionName, @PathParam("id") final String id) {
		final DtObject dto = dataServices.getObject(dtDefinitionName, id);
		return gson.toJson(dto);
	}

	private static URI createURI(final DtDefinition dtDefinition, final String id) {
		final DtField pk = DtObjectUtil.getPrimaryKey(dtDefinition);
		return new URI(dtDefinition, pk.getDomain().getFormatter().stringToValue(id, pk.getDomain().getDataType()));
	}

	@GET
	@Path("/{dtDefinitionName}")
	@Produces("application/json")
	public String getList(@PathParam("dtDefinitionName") final String dtDefinitionName) {
		final DtList dtList = dataServices.getList(dtDefinitionName);

		final List simpleList = new ArrayList();
		simpleList.addAll(dtList);
		return gson.toJson(simpleList);
	}

	@POST
	@Path("/{dtDefinitionName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String save(@PathParam("dtDefinitionName") final String dtDefinitionName, final String json) {
		final Status status = new Status();
		final DtDefinition dtDefinition = Home.getNameSpace().resolve(dtDefinitionName, DtDefinition.class);
		final DtObject dto;
		DtObjectConverter.SHARED.set(dtDefinition);
		try {
			dto = gson.fromJson(json, DtObject.class);
		} finally {
			//On remet tjrs à null pour éviter les pbs.
			DtObjectConverter.SHARED.set(null);
		}
		try {
			dataServices.save(dto);
			status.ok = true;
		} catch (final KUserException e) {
			status.errorMsg = e.getMessageText().getDisplay();
			// } catch (KRuntimeException e) {
			// status.errorMsg = e.getMessage();
			// } catch (Exception e) {
			// status.errorMsg = e.getMessage();
		}
		return gson.toJson(status);
	}

	@DELETE
	@Path("/{dtDefinitionName}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String delete(@PathParam("dtDefinitionName") final String dtDefinitionName, @PathParam("id") final String id) {
		final Status status = new Status();
		try {
			dataServices.delete(dtDefinitionName, id);
			status.ok = true;
		} catch (final KUserException e) {
			status.errorMsg = e.getMessageText().getDisplay();
			// } catch (KRuntimeException e) {
			// status.errorMsg = e.getMessage();
			// } catch (Exception e) {
			// status.errorMsg = e.getMessage();
		}
		return gson.toJson(status);
	}

	private static class Status {
		boolean ok;
		String errorMsg;
	}

}
