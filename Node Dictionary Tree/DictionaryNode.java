
public class DictionaryNode {
	private DictionaryNode[] connectedNodes;
	private String prefix;
	private boolean valid;
	
	public DictionaryNode(){
		connectedNodes = new DictionaryNode[26];
		valid = false;
		prefix = "";
	}
	
	public void setNode(DictionaryNode input, int index){
		this.connectedNodes[index] = input;
	}
	public DictionaryNode getNode(int index){
		return this.connectedNodes[index];
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
