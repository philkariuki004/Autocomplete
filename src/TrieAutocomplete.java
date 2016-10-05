import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 * modified by Philip Kariuki
 *
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws a
	 *             NullPointerException if either argument is null
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 * 
	 */
	private void add(String word, double weight) {
		Node current = myRoot;
		for (int i = 0; i < word.length(); i++) {
			if (current.mySubtreeMaxWeight < weight){
				current.mySubtreeMaxWeight = weight;

			}
			char ch = word.charAt(i);
			if (!current.children.containsKey(ch)) {
				current.children.put(ch, new Node(ch, current, weight));
			}
			current = current.getChild(ch);

		}
		current.isWord = true;
		current.setWord(word);
		current.setWeight(weight);
		if (current.mySubtreeMaxWeight < weight) {
			current.mySubtreeMaxWeight = weight;
		}

	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An array of the k words with the largest weights among all words
	 *         starting with prefix, in descending weight order. If less than k
	 *         such words exist, return an array containing all those words If
	 *         no such words exist, return an empty array
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public String[] topKMatches(String prefix, int k) {
		if (prefix.equals(null)) {
			throw new NullPointerException("Prefix is null");
		}
		Node current = myRoot;
		// Move upto prefix node.
		for (int i = 0; i < prefix.length(); i++) {
			if (!current.children.containsKey(prefix.charAt(i))) {
				return new String[0];
			} else {
				current = current.getChild(prefix.charAt(i));
				// System.out.println("word in for loop is "+current.myWord);
			}
		}
		
		Comparator<Node> myComp = new Node.ReverseSubtreeMaxWeightComparator();
		ArrayList<String> words = new ArrayList<String>();
		PriorityQueue<Node> myPQ = new PriorityQueue<Node>(myComp);
		// checks if the current node(Associated with the last character of
		// prefix)has any children.
		if (current.children.size() == 0) {
			if (current.isWord) {
				return new String[] { current.myWord };
			}
			return new String[0];

		}
		myPQ.offer(current);
		while (!myPQ.isEmpty()) {
			current = myPQ.poll();
			if (current != null && current.isWord) {
				words.add(current.myWord);
			}
			if (current != null && !current.children.values().isEmpty()) {
				for (Node node : current.children.values()) {
					myPQ.offer(node);
				}
			}

		}
		String[] toreturn = new String[Math.min(k, words.size())];
		for (int i = 0; i < toreturn.length; i++) {
			toreturn[i] = words.get(i);
		}

		return toreturn;
	}

	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from _terms with the largest weight starting with
	 *         prefix, or an empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 * 
	 */
	public String topMatch(String prefix) {
		if (prefix.equals(null)) {
			throw new NullPointerException("Prefix is null");
		}
		Node current = myRoot;
		// Go all the way to the last character of prefix:
		for (int i = 0; i < prefix.length(); i++){
			if (!current.children.containsKey(prefix.charAt(i))) {
				return "";
			} else {
				current = current.getChild(prefix.charAt(i));
			}
		}
		
		Map<Character, Node> myKids = current.children;
		
		// Check if current has any children.If true,Returns the prefix,if it
		// happens to be a word.
		if (myKids.size() == 0 || myKids == null) {
			if (current.isWord) {
				return prefix;
			} else {
				return "";
			}
		}
		Set<Character> mySet = myKids.keySet();
		// Check which node has a weight equal to current.mySubtreeMaxWeight
		for (Character character : mySet) {
			Node checkNode = myKids.get(character);
			if (checkNode.mySubtreeMaxWeight == current.mySubtreeMaxWeight) {
				current = checkNode;
				break;
			}
		}
		// To look for the node that is a word and also has subtreeMaxWeight of
		// current.
		while (!current.isWord) {
			Map<Character, Node> chMap = current.children;
			for (Node node : chMap.values()) {
				if (node.mySubtreeMaxWeight == current.mySubtreeMaxWeight) {
					current = node;
					break;
				}
			}
		}
		return current.getWord();
	}

}
