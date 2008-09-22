package com.collabnet.ccf.pi.cee.pt.v50;

public class ProjectTrackerHelper {
	private static ProjectTrackerHelper instance = null;
	private ProjectTrackerHelper(){
		
	}
	public static ProjectTrackerHelper getInstance(){
		synchronized(ProjectTrackerHelper.class){
			if(instance == null){
				instance = new ProjectTrackerHelper();
			}
		}
		return instance;
	}
	public String getArtifactIdFromFullyQualifiedArtifactId(String fullyQualifiedArtifactId){
		String artifactIdentifier =
			fullyQualifiedArtifactId.substring(fullyQualifiedArtifactId.lastIndexOf(":")+1);
		return artifactIdentifier;
	}
	public String getArtifactTypeTagNameFromFullyQualifiedArtifactId(String fullyQualifiedArtifactId){
		String artifactTypeTagName =
			fullyQualifiedArtifactId.substring(fullyQualifiedArtifactId.lastIndexOf("}")+1,
					fullyQualifiedArtifactId.lastIndexOf(":"));
		return artifactTypeTagName;
	}
	public String getArtifactTypeNamespaceFromFullyQualifiedArtifactId(String fullyQualifiedArtifactId){
		String artifactTypeNamespace =
			this.getArtifactTypeNamespaceFromFullyQualifiedArtifactType(fullyQualifiedArtifactId);
		return artifactTypeNamespace;
	}
	public String getArtifactTypeTagNameFromFullyQualifiedArtifactType(String fullyQualifiedArtifactType){
		String artifactTypeTagName =
			fullyQualifiedArtifactType.substring(fullyQualifiedArtifactType.lastIndexOf("}")+1);
		return artifactTypeTagName;
	}
	public String getArtifactTypeNamespaceFromFullyQualifiedArtifactType(String fullyQualifiedArtifactType){
		String artifactTypeNamespace =
			fullyQualifiedArtifactType.substring(1,fullyQualifiedArtifactType.lastIndexOf("}"));
		return artifactTypeNamespace;
	}
	public String getArtifactTypeNamespaceFromRepositoryId(String repositoryId){
		String artifactTypeNamespace = repositoryId.substring(repositoryId.indexOf('{')+1,
				repositoryId.indexOf('}'));
		return artifactTypeNamespace;
	}
	public String getArtifactTypeTagNameFromRepositoryId(String repositoryId){
		String artifactTypeNamespace = repositoryId.substring(repositoryId.indexOf('}')+1);
		return artifactTypeNamespace;
	}
	public String getNamespace(String input){
		int start = input.indexOf("{");
		int end = input.indexOf("}");
		String namespace = null;
		if(start >=0 && end >=2){
			namespace = input.substring(start+1, end);
		}
		return namespace;
	}
	public static String getNamespaceWithBraces(String input){
		int start = input.indexOf("{");
		int end = input.indexOf("}");
		String namespace = null;
		if(start >=0 && end >=2){
			namespace = input.substring(start, end+1);
		}
		else {
			namespace = "";
		}
		return namespace;
	}
	public String getEntityName(String input){
		int start = input.indexOf("}");
		int end = input.indexOf(":");
		String entityName = null;
		if(start < 0 && end < 0){
			entityName = input;
		}
		else if(start >= 0 && end < 0){
			entityName = input.substring(start+1);
		}
		else if(start >=0 && end >= 2){
			entityName = input.substring(start+1, end);
		}
		return entityName;
	}
}
