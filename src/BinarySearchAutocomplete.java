import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 
 * Using a sorted array of Term objects, this implementation uses binary search
 * to find the top term(s).
 * 
 * @author Austin Lu, adapted from Kevin Wayne
 *
 */
public class BinarySearchAutocomplete implements Autocompletor {

	Term[] myTerms;

	/**
	 * Given arrays of words and weights, initialize myTerms to a corresponding
	 * array of Terms sorted lexicographically.
	 * 
	 * This constructor is written for you, but you may make modifications to
	 * it.
	 * 
	 * @param terms
	 *            - A list of words to form terms from
	 * @param weights
	 *            - A corresponding list of weights, such that terms[i] has
	 *            weight[i].
	 * @return a BinarySearchAutocomplete whose myTerms object has myTerms[i] =
	 *         a Term with word terms[i] and weight weights[i].
	 * @throws a
	 *             NullPointerException if either argument passed in is null
	 */
	public BinarySearchAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		myTerms = new Term[terms.length];
		for (int i = 0; i < terms.length; i++) {
			myTerms[i] = new Term(terms[i], weights[i]);
		}
		Arrays.sort(myTerms);
	}

	/**
	 * Uses binary search to find the index of the first Term in the passed in
	 * array which is considered equivalent by a comparator to the given key.
	 * This method should not call comparator.compare() more than 1+log n times,
	 * where n is the size of a.
	 * 
	 * @param a
	 *            - The array of Terms being searched
	 * @param key
	 *            - The key being searched for.
	 * @param comparator
	 *            - A comparator, used to determine equivalency between the
	 *            values in a and the key.
	 * @return The first index i for which comparator considers a[i] and key as
	 *         being equal. If no such index exists, return -1 instead.
	 */
	public static int firstIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		int low = -1;
		int high = a.length;
		while (high - low > 1) {
			int mid = (low + high) / 2;

			if (comparator.compare(a[mid], key) < 0) {
				low = mid;
			} else if (comparator.compare(a[mid], key) == 0) {
				high = mid;
			} else {
				high = mid;
			}
		}
		// we need to make sure that high is not still a.length, which would
		// give us an out of bounds error.
		if (high <= a.length - 1 && comparator.compare(a[high], key) == 0) {
			return high;
		}
		// we need to make sure that low is not still -1, which would give us an
		// out of bounds error.
		if (low >= 0 && comparator.compare(a[low], key) == 0) {

			return low;
		} else {
			return -1;
		}
	}

	/**
	 * The same as firstIndexOf, but instead finding the index of the last Term.
	 * 
	 * @param a
	 *            - The array of Terms being searched
	 * @param key
	 *            - The key being searched for.
	 * @param comparator
	 *            - A comparator, used to determine equivalency between the
	 *            values in a and the key.
	 * @return The last index i for which comparator considers a[i] and key as
	 *         being equal. If no such index exists, return -1 instead.
	 */
	// Very similar logic as was used in firstIndexOf.
	public static int lastIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		int low = -1;
		int high = a.length;
		while (high - low > 1) {
			int mid = (low + high) / 2;
			if (comparator.compare(a[mid], key) < 0) {
				low = mid;
			} else if (comparator.compare(a[mid], key) == 0) {
				low = mid;
			} else {
				high = mid;
			}
		}
		// Avoid out of bounds errors.
		if (high <= a.length - 1 && comparator.compare(a[high], key) == 0) {
			return high;
		}
		// Avoid out of bounds errors.
		if (low >= 0 && comparator.compare(a[low], key) == 0) {

			return low;
		} else {
			return -1;
		}
	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in myTerms with the largest weight which match the given prefix,
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
		String[] myEmpty = {};
		if (myTerms.equals(null)) {
			throw new NullPointerException("no word");
		}
		// Using the firstInexOf and lastIndexOf methods decreases our run time.
		int first = firstIndexOf(myTerms, new Term(prefix, 0), new Term.PrefixOrder(prefix.length()));
		int last = lastIndexOf(myTerms, new Term(prefix, 0), new Term.PrefixOrder(prefix.length()));
		if (last >= first && first != -1) {
			PriorityQueue<Term> pq = new PriorityQueue<Term>(k, new Term.WeightOrder());
			// Much of the below code is the same as Brute but our for loop only
			// goes from first to last indexes which are the ones
			// that match the prefix.
			for (int i = first; i <= last; i++) {
				Term t = myTerms[i];
				if (pq.size() < k) {
					pq.add(t);
				} else if (pq.peek().getWeight() < t.getWeight()) {
					pq.remove();
					pq.add(t);
				}
			}
			int numResults = Math.min(k, pq.size());
			String[] ret = new String[numResults];
			for (int i = 0; i < numResults; i++) {
				ret[numResults - 1 - i] = pq.remove().getWord();
			}
			return ret;
		}
		return myEmpty;
	}

	@Override
	/**
	 * Given a prefix, returns the largest-weight word in myTerms starting with
	 * that prefix. e.g. for {air:3, bat:2, bell:4, boy:1}, topMatch("b") would
	 * return "bell". If no such word exists, return an empty String.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from myTerms with the largest weight starting with
	 *         prefix, or an empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 * 
	 */
	public String topMatch(String prefix) {
		String myEmpty = "";
		if (myTerms.equals(null)) {
			throw new NullPointerException("no word");
		}
		Term preTerm = new Term(prefix, 0);
		int first = firstIndexOf(myTerms, preTerm, new Term.PrefixOrder(prefix.length()));
		int last = lastIndexOf(myTerms, preTerm, new Term.PrefixOrder(prefix.length()));
		if (last >= first && first != -1) {
			String maxTerm = "";
			double maxWeight = -1;
			// Once again we only go from the first to last index which
			// decreases runtime.
			for (int i = first; i <= last; i++) {
				Term t = myTerms[i];
				if (t.getWeight() > maxWeight && t.getWord().startsWith(prefix)) {
					maxTerm = t.getWord();
					maxWeight = t.getWeight();
				}
			}
			return maxTerm;
		}
		return myEmpty;
	}
}
