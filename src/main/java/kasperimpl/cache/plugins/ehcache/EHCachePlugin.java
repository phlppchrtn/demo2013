package kasperimpl.cache.plugins.ehcache;

import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import kasper.codec.CodecManager;
import kasper.kernel.lang.Activeable;
import kasper.kernel.lang.Modifiable;
import kasper.kernel.manager.ManagerSummaryInfo;
import kasper.kernel.util.Assertion;
import kasperimpl.cache.CachePlugin;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

/**
 * Implémentation EHCache du CacheManager.
 *
 * @author pchretien
 */
public final class EHCachePlugin implements CachePlugin, Activeable {
	/**
	 * Formatter utilisé pour présenter les décimaux.
	 */
	private static final NumberFormat DOUBLE_FORMATTER = new DecimalFormat("#,##0.00");
	/**
	 * Formatter utilisé pour les nombres entiers (Integer et Long).
	 */
	private static final Format SIMPLE_FORMATTER = NumberFormat.getInstance();

	private net.sf.ehcache.CacheManager manager;
	private final CodecManager codecManager;
	private final Map<String, List<String>> cacheTypeMap = new LinkedHashMap<String, List<String>>();

	/**
	 * Constructeur.
	 * @param codecManager Manager des mécanismes de codage/décodage. 
	 */
	@Inject
	public EHCachePlugin(final CodecManager codecManager) {
		Assertion.notNull(codecManager);
		//---------------------------------------------------------------------
		this.codecManager = codecManager;

	}

	/** {@inheritDoc} */
	public void addCache(final String cacheType, final String context, final int maxElementsInMemory, final long timeToLiveSeconds, final long timeToIdleSeconds) {
		if (!manager.cacheExists(context)) {
			final boolean overflowToDisk = true;
			final boolean eternal = false;
			final net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(context, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
			manager.addCache(cache);
		}
		registerCacheType(context, cacheType);
	}

	/** {@inheritDoc} */
	public void put(final String context, final Serializable key, final Serializable value) {
		Assertion.precondition(!(value instanceof byte[]), "Ce CachePlugin ne permet pas de mettre en cache des byte[].");
		//---------------------------------------------------------------------
		//Si l'objet est bien marqué non modifiable (ie : interface Modifiable ET !isModifiable)
		//on peut le garder tel quel, sinon on le clone
		if (isUnmodifiable(value)) {
			putEH(context, key, value);
		} else {
			// Sérialisation avec compression
			final byte[] serializedObject = codecManager.getCompressedSerializationCodec().encode(value);
			//La sérialisation est équivalemnte à un deep Clone.
			putEH(context, key, serializedObject);
		}
	}

	private boolean isUnmodifiable(final Serializable value) {
		//s'il n'implemente pas Modifiable, il doit être cloné
		//s'il implemente Modifiable et que isModifiable == true, il doit être cloné 
		return value instanceof Modifiable && !((Modifiable) value).isModifiable();
	}

	/** {@inheritDoc} */
	public Serializable get(final String context, final Serializable key) {
		final Serializable cachedObject = getEH(context, key);
		//on ne connait pas l'état Modifiable ou non de l'objet, on se base sur son type.
		if (cachedObject instanceof byte[]) {
			final byte[] serializedObject = (byte[]) cachedObject;
			return codecManager.getCompressedSerializationCodec().decode(serializedObject);
		}
		return cachedObject;
	}

	/** {@inheritDoc} */
	public boolean remove(final String context, final Serializable key) {
		return getEHCache(context).remove(key);
	}

	/** {@inheritDoc} */
	public void clearAll() {
		manager.clearAll();
	}

	/** {@inheritDoc} */
	public void clear(final String context) {
		getEHCache(context).removeAll();
	}

	private void putEH(final String context, final Object key, final Serializable serialized) {
		final Element element = new Element(key, serialized);
		getEHCache(context).put(element);
	}

	private Serializable getEH(final String context, final Object key) {
		final Element element = getEHCache(context).get(key);
		return element == null ? null : element.getValue();
	}

	private Ehcache getEHCache(final String context) {
		final Ehcache ehCache = manager.getCache(context);
		Assertion.notNull(ehCache, "Le cache {0} n''a pas été déclaré. Ajouter le dans un fichier ehcache.xml dans le répertoire WEB-INF de votre webbapp.", context);
		return ehCache;
	}

	/** {@inheritDoc} */
	public void start() {
		manager = net.sf.ehcache.CacheManager.create();
	}

	/** {@inheritDoc} */
	public void stop() {
		manager.shutdown();
	}

	private void registerCacheType(final String context, final String cacheType) {
		List<String> cacheNameList = cacheTypeMap.get(cacheType);
		if (cacheNameList == null) {
			cacheNameList = new ArrayList<String>();
			cacheTypeMap.put(cacheType, cacheNameList);
		}
		cacheNameList.add(context);
	}

	//---------------------------------------------------------------------------
	//------------------Gestion du rendu et des interactions---------------------
	//---------------------------------------------------------------------------
	/*
	 * Desctiption d'un cache
	 */
	private void toHtml(final PrintStream out, final Ehcache cache) {
		final net.sf.ehcache.Statistics stats = cache.getStatistics();
		out.println("Nombre total d'objets dans le cache : ");
		final String br = "<br/>";
		out.print(SIMPLE_FORMATTER.format(stats.getObjectCount()));
		out.println(br);
		out.print("Nombre d'objets sur disque : ");
		out.print(SIMPLE_FORMATTER.format(cache.getDiskStoreSize()));
		out.println(br);
		out.print("Taille en mémoire : ");
		out.print(SIMPLE_FORMATTER.format(cache.calculateInMemorySize()));
		out.print(" Octets ");
		out.println(br);
		out.print("Efficacité cache mémoire (hits mémoire / hits) : ");
		out.print(SIMPLE_FORMATTER.format(stats.getInMemoryHits()));
		out.print(" / ");
		out.print(SIMPLE_FORMATTER.format(stats.getCacheHits()));
		out.print(" = ");
		final double ratio;
		if (stats.getCacheHits() > 0) {
			ratio = (double) stats.getInMemoryHits() * 100 / stats.getCacheHits();
			//Par convention 100%
		} else {
			ratio = 100d;
		}
		final CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
		out.print("<span style=\"background-color:#");
		if (ratio >= 80) {
			out.print("6F6");
		} else if (ratio >= 40) {
			out.print("F82");
		} else {
			out.print("F66");
		}
		out.print(";\">");
		out.print(DOUBLE_FORMATTER.format(ratio));
		out.print('%');
		out.print("</span>");
		out.println(br);
		out.print("Paramétrage : ");
		out.print("EHCache [ ");
		out.print("name = ");
		out.print(cache.getName());
		out.print(", isOverflowToDisk = ");
		out.print(cacheConfiguration.isOverflowToDisk());
		out.print(", maxElementsInMemory = ");
		out.print(SIMPLE_FORMATTER.format(cacheConfiguration.getMaxElementsInMemory()));
		out.print(", maxElementsOnDisk = ");
		out.print(SIMPLE_FORMATTER.format(cacheConfiguration.getMaxElementsOnDisk()));
		out.print(", timeToIdleSeconds = ");
		out.print(SIMPLE_FORMATTER.format(cacheConfiguration.getTimeToIdleSeconds()));
		out.print(", timeToLiveSeconds = ");
		out.print(SIMPLE_FORMATTER.format(cacheConfiguration.getTimeToLiveSeconds()));
		out.print(", eternal = ");
		out.print(cacheConfiguration.isEternal());
		out.println(']');
	}

	/** {@inheritDoc} */
	public void toHtml(final PrintStream out) {
		final Map<String, List<String>> cacheNameListMap = cacheTypeMap;
		for (final String cacheType : cacheTypeMap.keySet()) {
			out.print("<h2>Type de cache : ");
			out.print(cacheType);
			out.println("</h2>");
			out.print("<div class=\"");
			out.print(cacheType);
			out.println("\">");
			for (final String cacheName : cacheNameListMap.get(cacheType)) {
				out.print("<h3>Cache : ");
				out.print(cacheName);
				out.println("</h3>");
				final Ehcache cache = getEHCache(cacheName);
				toHtml(out, cache);
			}
			out.println("</div>");
		}
	}

	/** {@inheritDoc} */
	public ManagerSummaryInfo getMainSummaryInfo() {
		for (final String cacheType : cacheTypeMap.keySet()) {
			//on ne prend que le premier
			return getSummaryInfo(cacheTypeMap.get(cacheType), cacheType);
		}
		//si pas de cache
		final ManagerSummaryInfo managerSummaryInfo = new ManagerSummaryInfo();
		managerSummaryInfo.setInfo("pas de cache");
		return managerSummaryInfo;
	}

	/** {@inheritDoc} */
	public List<ManagerSummaryInfo> getSummaryInfos() {
		final List<ManagerSummaryInfo> summaryInfos = new ArrayList<ManagerSummaryInfo>();
		boolean isFirst = true;
		for (final String cacheType : cacheTypeMap.keySet()) {
			//on passe le premier qui est déja en MainSummaryInfo
			if (!isFirst) {
				summaryInfos.add(getSummaryInfo(cacheTypeMap.get(cacheType), cacheType));
			}
			isFirst = false;
		}
		return summaryInfos;
	}

	/** {@inheritDoc} */
	public String getName() {
		return "Cache";
	}

	/** {@inheritDoc} */
	public String getImage() {
		return "DB web.png";
	}

	/**
	 * Ratio entre l'utilisation mémoire et l'utilisation globale du cache.
	 * Si l'utilisation est fable celà signifie des I/O sur disque.
	 * @return Ratio de 0 à 100.
	 */
	private double getCacheHitRatio(final List<String> cacheNames) {
		long inMemoryHits = 0L;
		long cacheHits = 0L;

		for (final String cacheName : cacheNames) {
			final Ehcache cache = getEHCache(cacheName);
			final net.sf.ehcache.Statistics stats = cache.getStatistics();
			inMemoryHits += stats.getInMemoryHits();
			cacheHits += stats.getCacheHits();
		}

		final double ratio;
		if (cacheHits > 0) {
			ratio = (double) inMemoryHits * 100 / cacheHits;
		} else {
			//Par convention 100%
			ratio = 100d;
		}

		return ratio;
	}

	private ManagerSummaryInfo getSummaryInfo(final List<String> dataCacheNameList, final String cacheTypeLibelle) {
		final double ratio = getCacheHitRatio(dataCacheNameList);
		final ManagerSummaryInfo managerSummaryInfo = new ManagerSummaryInfo();
		managerSummaryInfo.setValue(ratio);
		managerSummaryInfo.setValueTitle("% hit ratio");
		managerSummaryInfo.setInfoTitle(dataCacheNameList.size() + " " + cacheTypeLibelle);
		return managerSummaryInfo;
	}
}
