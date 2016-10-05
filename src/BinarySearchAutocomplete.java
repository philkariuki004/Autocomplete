import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
		int i = -1;
		int mid = 0;
		int low = -1, high = a.length - 1;
		while (high - low > 1) {
			mid = (high + low) / 2;
			if (comparator.compare(a[mid], key) == 0) {// if equal
				high = mid;
			} else if (comparator.compare(a[mid], key) < 0) {// if a[mid] is
																// less than key
				low = mid;
			} else {
				high = mid;

			}
		}
		if (low >= 0 && comparator.compare(a[low], key) == 0) {
			i = low;
		} else {
			if (high >= 0 && comparator.compare(a[high], key) == 0) {
				i = high;
			}
		}
		return i;
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
	public static int lastIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		int i = -1;
		int mid = 0;
		int low = -1, high = a.length - 1;
		while (high - low > 1) {
			mid = (high + low) / 2;
			if (comparator.compare(a[mid], key) == 0) {
				low = mid;
			} else if (comparator.compare(a[mid], key) < 0) {
				low = mid;
			} else {
				high = mid;

			}
		}
		if (high >= 0 && comparator.compare(a[high], key) == 0) {
			i = high;
		} else {
			if (low >= 0 && comparator.compare(a[low], key) == 0) {
				i = low;
			}
		}

		return i;
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
	 *         no such words exist, return an empty array.
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public String[] topKMatches(String prefix, int k) {

		if (prefix.equals(null)) {
			throw new NullPointerException("Prefix is null");
		}
		Term preTerm = new Term(prefix, 1);
		Comparator<Term> mycomp = new Term.PrefixOrder(prefix.length());
		int first = firstIndexOf(myTerms, preTerm, mycomp);

		if (first < 0) {// if no term starts with prefix.
			return new String[0];
		}
		int last = lastIndexOf(myTerms, preTerm, mycomp);

		Term[] newterms = Arrays.copyOfRange(myTerms, first, last + 1);

		Arrays.sort(newterms, new Term.ReverseWeightOrder());

		String[] toReturn = new String[Math.min(k, newterms.length)];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = newterms[i].getWord();
		}
		return toReturn;
	}

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
		String topMatch = "";
		if (prefix.equals(null)) {
			throw new NullPointerException("Prefix is null");
		}
		Term preTerm = new Term(prefix, 1);
		int first = firstIndexOf(myTerms, preTerm, new Term.PrefixOrder(prefix.length()));
		if (first < 0) {// if term with prefix doesn't exist in myTerms.
			return "";
		}
		int last = lastIndexOf(myTerms, preTerm, new Term.PrefixOrder(prefix.length()));
		Term[] newTerms = Arrays.copyOfRange(myTerms, first, last + 1);
		Comparator<Term> mycomp = new Term.ReverseWeightOrder();
		Term max = newTerms[0];
		for (int i = 0; i < newTerms.length; i++) {
			if (mycomp.compare(newTerms[i], max) < 0) {
				max = newTerms[i];
			}
		}

		topMatch = max.getWord();

		return topMatch;
	}

}
