package org.purl.wf4ever.astrotaverna.vorepo;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import net.ivoa.wsdl.registrysearch.v1.ResolveResponse;
import net.ivoa.xml.adql.v1.AtomType;
import net.ivoa.xml.adql.v1.ClosedSearchType;
import net.ivoa.xml.adql.v1.ColumnReferenceType;
import net.ivoa.xml.adql.v1.ComparisonPredType;
import net.ivoa.xml.adql.v1.IntersectionSearchType;
import net.ivoa.xml.adql.v1.LikePredType;
import net.ivoa.xml.adql.v1.LiteralType;
import net.ivoa.xml.adql.v1.SearchType;
import net.ivoa.xml.adql.v1.StringType;
import net.ivoa.xml.adql.v1.UnionSearchType;
import net.ivoa.xml.adql.v1.WhereType;
import net.ivoa.xml.conesearch.v1.ConeSearch;
import net.ivoa.xml.registryinterface.v1.VOResources;
import net.ivoa.xml.sia.v1.SimpleImageAccess;
import net.ivoa.xml.ssa.v0.SimpleSpectralAccess;
import net.ivoa.xml.tapregext.v1.TableAccess;
import net.ivoa.xml.voresource.v1.Capability;
import net.ivoa.xml.voresource.v1.Resource;
import net.ivoa.xml.voresource.v1.Service;

import org.apache.log4j.*;

import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import org.purl.wf4ever.astrotaverna.wsdl.registrysearch.ErrorResp;
import org.purl.wf4ever.astrotaverna.wsdl.registrysearch.RegistrySearchPortType;
import org.purl.wf4ever.astrotaverna.wsdl.registrysearch.RegistrySearchService;

public class VORepository {
	private static final String DUMMY_SEARCH_WSDL = "/wsdl/dummySearch.wsdl";

	private static final String CAPABILITY_XSI_TYPE = "capability/@xsi:type";
	
	private static final String CAPABILITY_STANDAR_ID_TYPE = "capability/@standardID";
	
	private static final String ADDRESS_CONECTION_SEARCH_TAB = "ivo://ivoa.net/std/TAP";
	
	private static final String ADDRESS_CONECTION_SEARCH_SIA = "ivo://ivoa.net/std/SIA";
	
	private static final String ADDRESS_CONECTION_SEARCH_CONE = "ivo://ivoa.net/std/ConeSearch";
	
	private static final String ADDRESS_CONECTION_SEARCH_SSA = "ivo://ivoa.net/std/SSA";

	private static Logger logger = Logger.getLogger(VORepository.class);

	private static PropertyUtilsBean propertyUtils = new PropertyUtilsBean();

	public enum Status {
		OK, ERROR, CONNECTION_ERROR, UNKNOWN;
	}

	// We'll do similar fields as Topcat - see
	// http://www.ivoa.net/internal/IVOA/InterOpMay2010Reg/reg.pdf
	//protected List<String> KEYWORD_XPATHS = Arrays.asList("title", "shortName",
		//	"identifier", "content/subject", "content/description",
		//	"content/type");
	protected List<String> KEYWORD_NAMEPATH = Arrays.asList("title", "shortName",
			"identifier", "subject", "publisher");
	
	protected List<String> KEYWORD_XPATHS = Arrays.asList("title", "shortName",
				"identifier", "content/subject", "curation/publisher");
	
	protected final static QName REGISTRYSEARCHSERVICE_QNAME = new QName(
			"http://purl.org/wf4ever/astrotaverna/wsdl/RegistrySearch",
			"RegistrySearchService");

	public static final URI DEFAULT_ENDPOINT = URI
			//.create("http://nvo.stsci.edu/vor10/ristandardservice.asmx");
			//.create("http://registry.euro-vo.org/services/RegistrySearch");
			.create("http://registry.astrogrid.org/astrogrid-registry/services/RegistryQueryv1_0");

	public static final URI WSDL = URI
			.create("http://www.ivoa.net/wsdl/RegistrySearch/v1.0");

	private URI endpoint;

	private RegistrySearchPortType port;

	public VORepository(URI endpoint) {
		setEndpoint(endpoint);
	}

	public VORepository() {
		setEndpoint(null);
	}

	public URI getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(URI endpoint) {
		if (endpoint == null) {
			endpoint = DEFAULT_ENDPOINT;
		}
		synchronized (this) {
			this.endpoint = endpoint;
			// Force getPort() to re-evaluate
			this.port = null;
		}
	}

	public Status getStatus() {
		RegistrySearchPortType port = getPort();
		ResolveResponse id;
		try {
			id = port.getIdentity("");
		} catch (WebServiceException e) {
			logger.warn("Could not get status from " + getEndpoint(), e);
			return Status.CONNECTION_ERROR;
		} catch (ErrorResp e) {
			logger.info("Error status from " + getEndpoint(), e);
			return Status.ERROR;
		}
		try {
			String status = id.getResource().getStatus();
			if (status.equalsIgnoreCase("active")) {
				return Status.OK;
			} else {
				logger.info("Unknown status: " + status + " from "
						+ getEndpoint());
				return Status.UNKNOWN;
			}
		} catch (NullPointerException ex) {
			logger.error(
					"Could not find resource status from " + getEndpoint(), ex);
			return Status.ERROR;
		}

	}

	protected RegistrySearchPortType getPort() {
		synchronized (this) {
			if (this.port != null) {
				return this.port;
			}
		}
		URL wsdlUri = getClass().getResource(DUMMY_SEARCH_WSDL);

		RegistrySearchService service = new RegistrySearchService(wsdlUri,
				REGISTRYSEARCHSERVICE_QNAME);
		RegistrySearchPortType port = service.getRegistrySearchPortSOAP();
		// Change binding hack as of
		// http://stackoverflow.com/questions/5158537/jaxws-how-to-change-the-endpoint-address
		// https://github.com/aviramsegal/snippets/blob/master/websphere/JaxWSCustomEndpoint.java
		BindingProvider bp = (BindingProvider) port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				getEndpoint().toASCIIString());

		synchronized (this) {
			this.port = port;
		}
		return port;
	}

	public List<Resource> keywordSearch(String keywords) throws ErrorResp {
		VOResources voResources = getPort().keywordSearch(keywords, true, null,
				null, null);
		return voResources.getResource();
	}

	public List<Service> resourceSearch(
			Class<? extends Capability> capabilityType,  String... keywords)
			throws ErrorResp {
		WhereType where = new WhereType();

		XmlType xmlType = capabilityType
				.getAnnotation(javax.xml.bind.annotation.XmlType.class);
		String xsiTypeValueLiteral = "%" + xmlType.name();
		// TODO: namespaced xsi:type lookup without using % trick?
		// Use capability/@standardID == "ivo://ivoa.net/std/ConeSearch" etc
		// instead?
		logger.info("Literal xsiTypeValueLiteral: " + xsiTypeValueLiteral.toString());
	
		List<SearchType> and = new ArrayList<SearchType>();
		//and.add(makeComparisonCondition(CAPABILITY_STANDAR_ID_TYPE,ADDRESS_CONECTION_SEARCH_TAB ));
		if (xsiTypeValueLiteral.compareTo("%TableAccess")==0)
			and.add(makeComparisonCondition(CAPABILITY_STANDAR_ID_TYPE,ADDRESS_CONECTION_SEARCH_TAB ));
		if ( xsiTypeValueLiteral.compareTo("%ConeSearch")==0)
			and.add(makeComparisonCondition(CAPABILITY_STANDAR_ID_TYPE,ADDRESS_CONECTION_SEARCH_CONE ));
		if ( xsiTypeValueLiteral.compareTo("%SimpleImageAccess")==0)
			and.add(makeComparisonCondition(CAPABILITY_STANDAR_ID_TYPE,ADDRESS_CONECTION_SEARCH_SIA ));
		if ( xsiTypeValueLiteral.compareTo("%SimpleSpectralAccess")==0)
			and.add(makeComparisonCondition(CAPABILITY_STANDAR_ID_TYPE,ADDRESS_CONECTION_SEARCH_SSA ));
		//and.add(makeLikeCondition(CAPABILITY_XSI_TYPE, xsiTypeValueLiteral));*/

		// This does not work for some reason
		// and.add(makeLikeCondition("capability/interface/@xsi:type",
		// "%HTTP%"));
// TODO:
		
		String name;
		int index;
		for (String kw : keywords) {
			List<SearchType> or = new ArrayList<SearchType>();
				if (kw.length()>0)
				{
					index = 0;
					for (String xpath : KEYWORD_XPATHS) {
						// NOTE: If keywordXpaths.size() == 1 - don't use the
						// intermediary or
						name = KEYWORD_NAMEPATH.get(index).toString();
						or.add(makeLikeCondition(name, xpath, "%" + kw + "%"));
						index ++;
					}
				}
			if (!or.isEmpty())
				and.add(makeConditionSearchType(UnionSearchType.class, or));
		}
		where.setCondition(makeConditionSearchType(
				IntersectionSearchType.class, and));

		logger.debug("Searching for " + capabilityType.getSimpleName() + ": "
				+ keywords);
		// Perform search
		List<Resource> resources = getPort().search(where, null, null, false)
				.getResource();

		// Just double-checking the capabilities as xsi:type check above is
		// fragile
		// Bonus: Convert to List<Service>
		List<Service> services = new ArrayList<Service>();
		for (Resource res : resources) {
			if (!(res instanceof Service)) {
				logger.info("Skipping non-Service " + res);
				continue;
			}
			Service ser = (Service) res;
			for (Capability c : ser.getCapability()) {
				try{
									
					if (capabilityType.isInstance(c) || (capabilityType.getName().contains("TableAccess") && c.getStandardID().contains("TAP"))) {
						services.add(ser);
						logger.debug("Found " + ser);
						break;
					}
				}catch(Exception ex){
					logger.info("Error al obtener los capability");
					
				}
			}
			logger.debug("Capability " + capabilityType.getSimpleName()
					+ " not found in " + res);
		}
		return services;
	}

	@SuppressWarnings("unchecked")
	protected SearchType makeConditionSearchType(
			Class<? extends SearchType> searchType, List<SearchType> conditions) {
		ClosedSearchType closed = new ClosedSearchType();
		if (conditions.isEmpty()) {
			throw new IllegalArgumentException(
					"Can't query empty list of conditions");
		}
		if (conditions.size() == 1) {
			closed.setCondition(conditions.get(0));
		} else {
			SearchType search;
			List<SearchType> list;
			try {
				search = searchType.newInstance();
				list = (List<SearchType>) propertyUtils
						.getProperty(search, "condition");
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't make " + searchType,
						e);
			}
			list.add(conditions.get(0));
			list.add(makeConditionSearchType(searchType,
					conditions.subList(1, conditions.size())));
			closed.setCondition(search);
		}
		return closed;
	}

	protected LikePredType makeLikeCondition(String name, String xpath, String literal) {
		LikePredType like = new LikePredType();
		ColumnReferenceType typeArg = new ColumnReferenceType();
		// TODO: Is this temp name really needed?
		typeArg.setName(name);
		typeArg.setTable("");
		typeArg.setXpathName(xpath);
		like.setArg(typeArg);
		AtomType pattern = new AtomType();
		StringType value = new StringType();
		value.setValue(literal);
		pattern.setLiteral(value);
		like.setPattern(pattern);
		return like;
	}
	
	protected ComparisonPredType makeComparisonCondition(String xpath, String literal ){
		//ClosedSearchType closed = new ClosedSearchType();
		ComparisonPredType comparison = new ComparisonPredType();
		comparison.setComparison("=");
		
		
		ColumnReferenceType typeArg = new ColumnReferenceType();
		typeArg.setTable("");
		typeArg.setName("@standardID");
		typeArg.setXpathName(xpath);
		
		AtomType atomType = new AtomType();
		StringType value = new StringType();
		value.setValue(literal);
		atomType.setLiteral(value);
		
		comparison.getArg().add(typeArg);
		comparison.getArg().add(atomType);
		
		//closed.setCondition(comparison);
		
		return comparison;
	}
	
	protected ComparisonPredType makeComparisonSearchCondition(String column, String xpath, String literal ){
		//ClosedSearchType closed = new ClosedSearchType();
		ComparisonPredType comparison = new ComparisonPredType();
		comparison.setComparison("=");
		
		
		ColumnReferenceType typeArg = new ColumnReferenceType();
		typeArg.setTable("");
		typeArg.setName("@standardID");
		typeArg.setXpathName(xpath);
		
		AtomType atomType = new AtomType();
		StringType value = new StringType();
		value.setValue(literal);
		atomType.setLiteral(value);
		
		comparison.getArg().add(typeArg);
		comparison.getArg().add(atomType);
		
		//closed.setCondition(comparison);
		
		return comparison;
	}
	

}
