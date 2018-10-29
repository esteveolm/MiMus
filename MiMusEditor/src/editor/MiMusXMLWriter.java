package editor;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import model.EntitiesList;
import model.Entity;
import model.Lemma;
import model.LemmasList;
import model.MiMusReference;
import model.MiMusText;
import model.ReferencesList;
import model.Relation;
import model.TypedEntity;
import model.UntypedEntity;

public class MiMusXMLWriter {
	
	private MiMusText regest;
	private MiMusText transcription;
	private EntitiesList regestEntities;
	private EntitiesList transcriptionEntities;
	private LemmasList lemmas;
	private ReferencesList references;
	private String docID;
	private org.w3c.dom.Document doc;
	private boolean created;

	public MiMusXMLWriter(MiMusText regest, MiMusText transcription, EntitiesList regestEntities,
			EntitiesList transcriptionEntities, LemmasList lemmas, ReferencesList references, String docID) {
		this.regest = regest;
		this.transcription = transcription;
		this.regestEntities = regestEntities;
		this.transcriptionEntities = transcriptionEntities;
		this.lemmas = lemmas;
		this.references = references;
		this.docID = docID;
		this.doc = null;
		this.created = false;
	}
	
	public boolean create() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = docBuilder.newDocument();
			
			Element tagDocument = doc.createElement("document");
			doc.appendChild(tagDocument);
			
			Element tagID = doc.createElement("doc_id");
			tagID.appendChild(doc.createTextNode(docID));
			tagDocument.appendChild(tagID);
			
			Element tagRegest = doc.createElement("regest");
			tagRegest.appendChild(doc.createTextNode(regest.getText()));
			tagDocument.appendChild(tagRegest);
			
			Element tagTranscription = doc.createElement("transcription");
			tagTranscription.appendChild(doc.createTextNode(transcription.getText()));
			tagDocument.appendChild(tagTranscription);
			
			/* Regest entities */
			Element tagRegestEntities = doc.createElement("regest_entities");
			tagDocument.appendChild(tagRegestEntities);
			for (Entity ent: regestEntities.getUnits()) {
				TypedEntity typedEnt = (TypedEntity) ent;
				Element tagIDEnt = doc.createElement("entity_id");
				tagIDEnt.appendChild(doc.createTextNode(String.valueOf(typedEnt.getId())));
				Element tagFromEnt = doc.createElement("from");
				tagFromEnt.appendChild(doc.createTextNode(String.valueOf(typedEnt.getFrom())));
				Element tagToEnt = doc.createElement("to");
				tagToEnt.appendChild(doc.createTextNode(String.valueOf(typedEnt.getTo())));
				Element tagTextEnt = doc.createElement("text");
				tagTextEnt.appendChild(doc.createTextNode(typedEnt.getText()));
				Element tagTypeEnt = doc.createElement("type");
				tagTypeEnt.appendChild(doc.createTextNode(typedEnt.getTypeWord()));
				Element tagSubtypeEnt = doc.createElement("subtype");
				tagSubtypeEnt.appendChild(doc.createTextNode(typedEnt.getSubtypeWord()));
				Element tagEntity = doc.createElement("entity");
				tagEntity.appendChild(tagIDEnt);
				tagEntity.appendChild(tagFromEnt);
				tagEntity.appendChild(tagToEnt);
				tagEntity.appendChild(tagTextEnt);
				tagEntity.appendChild(tagTypeEnt);
				tagEntity.appendChild(tagSubtypeEnt);
				tagRegestEntities.appendChild(tagEntity);
			}
			
			/* Transcription entities */
			Element tagTranscriptionEntities = doc.createElement("transcription_entities");
			tagDocument.appendChild(tagTranscriptionEntities);
			for (Entity ent: transcriptionEntities.getUnits()) {
				UntypedEntity untypedEnt = (UntypedEntity) ent;
				Element tagIDEnt = doc.createElement("entity_id");
				tagIDEnt.appendChild(doc.createTextNode(String.valueOf(untypedEnt.getId())));
				Element tagFromEnt = doc.createElement("from");
				tagFromEnt.appendChild(doc.createTextNode(String.valueOf(untypedEnt.getFrom())));
				Element tagToEnt = doc.createElement("to");
				tagToEnt.appendChild(doc.createTextNode(String.valueOf(untypedEnt.getTo())));
				Element tagTextEnt = doc.createElement("text");
				tagTextEnt.appendChild(doc.createTextNode(untypedEnt.getText()));
				Element tagEntity = doc.createElement("entity");
				tagEntity.appendChild(tagIDEnt);
				tagEntity.appendChild(tagFromEnt);
				tagEntity.appendChild(tagToEnt);
				tagEntity.appendChild(tagTextEnt);
				tagTranscriptionEntities.appendChild(tagEntity);
			}
			
			/* Lemmatizations */
			Element tagLemmatizations = doc.createElement("lemmatizations");
			tagDocument.appendChild(tagLemmatizations);
			for (Relation r: lemmas.getUnits()) {
				Lemma lem = (Lemma) r;
				Element tagTranscriptionId = doc.createElement("transcription_id");
				tagTranscriptionId.appendChild(doc.createTextNode(String.valueOf(lem.getTranscriptionEntityObject().getId())));
				Element tagRegestId = doc.createElement("regest_id");
				tagRegestId.appendChild(doc.createTextNode(String.valueOf(lem.getRegestEntityObject().getId())));
				Element tagLemmatization = doc.createElement("lemmatization");
				tagLemmatization.appendChild(tagTranscriptionId);
				tagLemmatization.appendChild(tagRegestId);
				tagLemmatizations.appendChild(tagLemmatization);
			}
			
			/* References */
			Element tagReferences = doc.createElement("references");
			tagDocument.appendChild(tagReferences);
			for (MiMusReference ref: references.getUnits()) {
				System.out.println(ref.toString() + " " + ref.getId());
				Element tagReferenceId = doc.createElement("ref_id");
				tagReferenceId.appendChild(doc.createTextNode(String.valueOf(ref.getId())));
				Element tagBiblioId = doc.createElement("biblio_id");
				tagBiblioId.appendChild(doc.createTextNode(String.valueOf(ref.getBibEntry().getId())));
				Element tagPages = doc.createElement("pages");
				tagPages.appendChild(doc.createTextNode(ref.getPage()));
				Element tagReference = doc.createElement("reference");
				tagReference.appendChild(tagReferenceId);
				tagReference.appendChild(tagBiblioId);
				tagReference.appendChild(tagPages);
				tagReferences.appendChild(tagReference);
			}
			
			this.doc = doc;
			this.created = true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.out.println("Could not create XML Document for Writing.");
			this.created = false;
		} catch (DOMException | IllegalTextRangeException e) {
			e.printStackTrace();
			System.out.println("Could not create XML Document for Writing.");
			this.created = false;
		}
		return this.created;
	}
	
	public boolean write(String path) {
		if (this.created) {
			try {
				/* Converts Java XML Document to file-system XML */
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
		        DOMSource source = new DOMSource(this.doc);
		        
		        File f = new File(path);
		        StreamResult console = new StreamResult(f);
		        transformer.transform(source, console);
		        return true;
			} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
				e.printStackTrace();
				System.out.println("Could not write XML.");
				return false;
			} catch (TransformerException e) {
				e.printStackTrace();
				System.out.println("Could not write XML.");
				return false;
			}
		} else {
			System.out.println("Must create() XML Document before write().");
			return false;
		}
	}
	
	public MiMusText getRegest() {
		return regest;
	}
	public void setRegest(MiMusText regest) {
		this.regest = regest;
	}
	public MiMusText getTranscription() {
		return transcription;
	}
	public void setTranscription(MiMusText transcription) {
		this.transcription = transcription;
	}
	public EntitiesList getRegestEntities() {
		return regestEntities;
	}
	public void setRegestEntities(EntitiesList regestEntities) {
		this.regestEntities = regestEntities;
	}
	public EntitiesList getTranscriptionEntities() {
		return transcriptionEntities;
	}
	public void setTranscriptionEntities(EntitiesList transcriptionEntities) {
		this.transcriptionEntities = transcriptionEntities;
	}
	public LemmasList getLemmas() {
		return lemmas;
	}
	public void setLemmas(LemmasList lemmas) {
		this.lemmas = lemmas;
	}
	public String getDocID() {
		return docID;
	}
	public void setDocID(String docID) {
		this.docID = docID;
	}
	public org.w3c.dom.Document getDoc() {
		return doc;
	}
	public void setDoc(org.w3c.dom.Document doc) {
		this.doc = doc;
	}
	public boolean isCreated() {
		return created;
	}
	public void setCreated(boolean created) {
		this.created = created;
	}
}
