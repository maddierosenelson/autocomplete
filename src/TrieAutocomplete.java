import java.util.PriorityQueue;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
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
	 * representing word.
	 * 
	 * Set the value of myWord, myWeight, isWord, and mySubtreeMaxWeight of the
	 * node corresponding to the added word to the correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 * 
	 */
	private void add(String word, double weight) {
		Node current = myRoot;
		if (word.equals(null)) {
			throw new NullPointerException("no word");
		}
		if (weight < 0) {
			throw new IllegalArgumentException("negative weight");
		}
		for (int i = 0; i < word.length(); i++) {
			char nextCh = word.charAt(i);
			if (current.mySubtreeMaxWeight < weight) {
				current.mySubtreeMaxWeight = weight;
			}
			if (!current.children.containsKey(nextCh)) {
				current.children.put(nextCh, new Node(nextCh, current, 0));
			}
			current = current.children.get(nextCh);
		}
		// isWord we want to be true when we are at the end of the word.
		current.isWord = true;
		current.myWord = word;
		current.myWeight = weight;
		if (current.mySubtreeMaxWeight <= weight) {
			current.mySubtreeMaxWeight = weight;
		} else {
			while (current != null) {
				current.mySubtreeMaxWeight = current.myWeight;
				// This is where we must go through and update the
				// mySubtreeMaxWeight
				// and deal with the duplicates by updating the weights.
				for (Node child : current.children.values()) {
					if (current.mySubtreeMaxWeight < child.mySubtreeMaxWeight) {
						current.mySubtreeMaxWeight = child.mySubtreeMaxWeight;
					}
				}
				current = current.parent;
			}
		}
	}

	@Override
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
		Node current = myRoot;
		String[] myEmpty = {};
		// PriorityQueue's work like lists except you take from one end and add
		// to the other.
		PriorityQueue<Node> pq = new PriorityQueue<Node>(k, new Node.ReverseSubtreeMaxWeightComparator());
		// We want this second Queue to be ordered from lowest to heighest
		// SubtreeMaxWeight, which is the default in this situation.
		PriorityQueue<Node> pq2 = new PriorityQueue<Node>(k);
		if (prefix == null) {
			throw new NullPointerException("no prefix");
		}
		// the below for loop will get us to the node where the prefix ends.
		for (int i = 0; i < prefix.length(); i++) {
			char nextCh = prefix.charAt(i);
			if (current.children.containsKey(nextCh)) {
				current = current.children.get(nextCh);
			} else {
				return myEmpty;
			}
		}
		pq.add(current);
		// We never want our pq2 to be larger than size k to keep our runtimes
		// lower. Thus we need to check the size of pq2.
		while (!pq.isEmpty()) {
			if (pq2.size() == k) {
				if (pq2.peek().myWeight >= pq.peek().mySubtreeMaxWeight) {
					break;
				} else {
					pq2.poll();
				}
			}
			current = pq.poll();
			// The below line goes through the entire map for the key current.
			for (Node child : current.children.values()) {
				pq.add(child);
			}
			// we only want to add to pq2 when we know we are at the final node
			// of a word, so if the boolean isWord is true.
			if (current.isWord) {
				pq2.add(current);
			}
		}
		String[] myAnswer = new String[pq2.size()];
		for (int i = pq2.size() - 1; i > -1; i--) {
			myAnswer[i] = pq2.poll().myWord;
		}
		return myAnswer;
	}

	@Override
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
		Node current = myRoot;
		if (prefix.equals(null)) {
			throw new NullPointerException("no word");
		}
		for (int i = 0; i < prefix.length(); i++) {
			char nextCh = prefix.charAt(i);
			if (current.children.containsKey(nextCh)) {
				current = current.children.get(nextCh);
			} else {
				return "";
			}
		}
		if (current.mySubtreeMaxWeight == current.myWeight) {
			return current.myWord;
		} else {
			while (current.mySubtreeMaxWeight != current.myWeight) {
				for (Node child : current.children.values()) {
					if (current.mySubtreeMaxWeight == child.mySubtreeMaxWeight) {
						current = child;
						// we want to break below because if we found the child
						// with the same subTreeMaxWeight as the current
						// we do not want to continue looping through the other
						// children.
						break;
					}
				}
			}
		}
		return current.myWord;
	}
}