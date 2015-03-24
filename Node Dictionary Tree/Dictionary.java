import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Dictionary {
	
	public DictionaryNode root;
	private boolean isEmpty;
	// Creates an empty dictionary
	public Dictionary(){
		isEmpty = true;
		root = new DictionaryNode();
	}
	
	// Creates a dictionary from a file
	public Dictionary(String filename) throws Exception{
		isEmpty = false;
		root = new DictionaryNode();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String word;
		while ((word = reader.readLine()) != null){
			add(word);
		}
		reader.close();
		
	}
	
	//Adds a word into the dictionary. Doesn't add to root, but
	// adds to root's children
	public void add(String word){
		if (isEmpty){
			isEmpty = false;
		}
		int index = indexOfFirst(word);
		DictionaryNode temp;
		if (root.getNode(index) == null){
			temp = new DictionaryNode();
		}
		else {
			temp = root.getNode(index);
		}
		temp = add(word, temp);
		root.setNode(temp, indexOfFirst(temp.getPrefix()));
	}
	
	private DictionaryNode add(String word, DictionaryNode inputNode) {
		if (inputNode.getPrefix().isEmpty()){
			DictionaryNode temp = new DictionaryNode();
			temp.setPrefix(word);
			temp.setValid(true);
			return temp;
		}
		else if (inputNode.getPrefix().equalsIgnoreCase(word)){
			if (inputNode.isValid() == true){
				return inputNode;
			}
			else {
				inputNode.setValid(true);
				return inputNode;
			}
		}
		else {
			String prefix = getLongestPrefix(inputNode.getPrefix(), word);
			String suffix = getSuffix(prefix, inputNode.getPrefix());
			String suffixWord = getSuffix(prefix, word);

			int tempIndex;
			DictionaryNode temp;
			
			// If the suffix of prefix and node is empty, ie insert 'wagon' then 'wagons'
			if (suffix.isEmpty()){
				tempIndex = indexOfFirst(suffixWord);
				if (inputNode.getNode(tempIndex) == null){
					temp = new DictionaryNode();
				}
				else {
					temp = inputNode.getNode(tempIndex);
				}
				temp = add(suffixWord, temp);
				inputNode.setNode(temp, tempIndex);
				return inputNode;
			}
			// If the suffix of word is empty, ie insert 'bugs' and then 'bug'
			else if (suffixWord.isEmpty()){
				tempIndex = indexOfFirst(suffix);
				if (inputNode.getNode(tempIndex) == null){
					temp = new DictionaryNode();
				}
				else {
					temp = inputNode.getNode(tempIndex);
				}
				temp = add(suffix, temp);
				inputNode.setNode(temp, tempIndex);
				inputNode.setPrefix(prefix);
				return inputNode;
			}
			// Suffix of word, prefix, and suffix of node are all non-empty. 
			else {
				tempIndex = indexOfFirst(suffix);
				temp = new DictionaryNode();
				temp.setPrefix(prefix);
				inputNode.setPrefix(suffix);
				
				temp.setNode(inputNode, tempIndex);
				int index = indexOfFirst(suffixWord);
				DictionaryNode recurseNode = new DictionaryNode();
				recurseNode = add(suffixWord, recurseNode);
				temp.setNode(recurseNode, index);
				return temp;				
			}
		}
	}
	
	private String getSuffix(String prefix, String word){
		return word.substring(prefix.length());
	}
	private String getLongestPrefix(String word1, String word2){
		
		String[] smallerString, biggerString;
		if (word1.length() < word2.length()) {
			smallerString = word1.split("");
			biggerString = word2.split("");
		}
		else if (word2.length() < word1.length()) {
			smallerString = word2.split("");
			biggerString = word1.split("");
		}
		else {
			smallerString = word1.split("");
			biggerString = word2.split("");
		}
		StringBuilder prefix = new StringBuilder(smallerString.length);
		for (int i = 0; i < smallerString.length; i++) {
			if (smallerString[i].equalsIgnoreCase(biggerString[i])) {
				prefix.append(smallerString[i]);
			}
			else {
				break;
			}
		}
		return prefix.toString();
	}
	
	//Checks to see if a prefix matches a word in the dictionary
	public boolean check(String word){
		boolean hasWord = false;
		if (isEmpty){
			return false;
		}
		else {
			if (word.isEmpty()){
				return false;
			}
			word = word.toLowerCase();
			int index = indexOfFirst(word);
			if (root.getNode(index) != null){
				hasWord = check(word, root.getNode(index));
			}
		}
		return hasWord;
	}
	
	private boolean check(String word, DictionaryNode node){
		if ( !word.startsWith(node.getPrefix()) ) {
			return false;
		}
		else if (word.equalsIgnoreCase(node.getPrefix()) && !node.isValid() ){
			return false;
		}
		else if (word.equalsIgnoreCase(node.getPrefix()) && node.isValid() ){
			return true;
		}
		else {
			String suffix = getSuffix(node.getPrefix(), word);
			int index = indexOfFirst(suffix);
			if (node.getNode(index) != null){
				return check(suffix, node.getNode(index));	
			}
			return false;
		}
	}
	
	public boolean checkPrefix(String prefix){
		boolean hasPrefix = false;
		if (isEmpty){
			return false;
		}
		else {
			if (prefix.isEmpty()){
				return true;
			}
			prefix = prefix.toLowerCase();
			int index = indexOfFirst(prefix);
			if (root.getNode(index) != null){
				hasPrefix = checkPrefix(prefix, root.getNode(index), ""); 
			}
		}
		return hasPrefix;
	}
	
	private boolean checkPrefix(String prefix, DictionaryNode node, String currWord){
		currWord = currWord + node.getPrefix();
		if (currWord.startsWith(prefix)){
			return true;
		}
		else {
			if (currWord.length() > prefix.length()){
				return false;
			}
			String suffix = getSuffix(currWord, prefix);
			if (suffix.isEmpty()){
				return false;
			}
			int index = indexOfFirst(suffix);
			if (node.getNode(index) != null){
				return checkPrefix(prefix, node.getNode(index), currWord);
			}
			else {
				return false;
			}
		}
	}
	//Prints out the contents of the dictionary, in alphabetical order, one word per line.
	public void print(){
		for (int i = 0; i < 26; i++){
			if (root.getNode(i) != null){
				print(root.getNode(i), "");
			}
		}
	}
	

	private void print(DictionaryNode inputNode, String currWord){
		String builtWord = currWord.toString() + inputNode.getPrefix();
		if (inputNode.isValid()){
			System.out.println(builtWord);
			for (int i = 0; i < 26; i++){
				if (inputNode.getNode(i) != null){
					print(inputNode.getNode(i), builtWord);
				}
			}
		}
		else {
			for (int i = 0; i < 26; i++){
				if (inputNode.getNode(i) != null){
					print(inputNode.getNode(i), builtWord);
				}
			}
		}
		
	}
	
	//Prints out the tree structure of the dictionary in pre-order form
	public void printTree(){
		for (int i = 0; i < 26; i++){
			if (root.getNode(i) != null){
				printTree(root.getNode(i), 0);
			}
		}
	}
	
	//Prints out the tree structure of the dictionary in pre-order form
	private void printTree(DictionaryNode inputNode, int offset){
		String text = "";
		for (int i = 0; i < offset; i++){
			text += " ";
		}
		text += inputNode.getPrefix();
		if (inputNode.isValid()){
			text += "<T>";
		}
		System.out.println(text);
		for (int i = 0; i < 26; i++){
			if (inputNode.getNode(i) != null){
				int localOffset = offset + 1;
				printTree(inputNode.getNode(i), localOffset);
			}
		}
	}
	
	/*
	 * JavaDoc - gets the ascii prefix # of the input word
	 */
	
	private int indexOfFirst(String word){
		char firstChar = word.charAt(0);
		return (int)firstChar - (int) 'a';
	}
	//Returns an array of the entries in the dictionary that are as close as possible to the input word
	public String[] suggest(String word, int numSuggestions){
		String[] suggestionList;
		if (check(word)){
			suggestionList = new String[1];
			suggestionList[0] = word;
			return suggestionList;
		}
		else {
			String prefix = "";
			for (int i = 0; i < word.length(); i++){
				if (checkPrefix(word.substring(0, i))){
					prefix = word.substring(0, i);
				}
				else {
					break;
				}
			}
			suggestionList = new String[numSuggestions];
			int index = indexOfFirst(prefix);
			suggest(prefix, suggestionList, root.getNode(index), "");
		}
		
		return suggestionList;
	}
	
	private void suggest(String word, String[] suggestions, DictionaryNode node, String currWord){
		if (suggestions[suggestions.length - 1] != null){
			return;
		}
		String builtWord = currWord.toString() + node.getPrefix();
		if (word.startsWith(node.getPrefix(), currWord.length())){
			String suffix = getSuffix(builtWord, word);
			if (!suffix.isEmpty()){
				int index = indexOfFirst(suffix);
				if (node.getNode(index) != null){
					suggest(word, suggestions, node.getNode(index), builtWord);
				}
			}
			else {
				if (node.isValid()){
					for (int i = 0; i < suggestions.length; i++){
						if (suggestions[i] != null){
							suggestions[i] = builtWord;
							break;
						}
					}
				}
			}
			for (int i = 0; i < 26; i++){
				if (node.getNode(i) != null && suggestions[suggestions.length - 1] == null){
					suggest(word, suggestions, node.getNode(i), builtWord);
				}
			}
		}
		else {
			for (int i = 0; i < 26; i++){
				if (node.getNode(i) != null){
					suggest(word, suggestions, node.getNode(i), builtWord);
				}
			}
			if (node.isValid()){
				for (int i = 0; i < suggestions.length; i++){
					if (suggestions[i] == null){
						suggestions[i] = builtWord;
						break;
					}
				}
			}
		}
	}
}
