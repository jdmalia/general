import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Represent a Huffman Encoder/Decoder
 * 
 * @author Jason Malia
 */

public class HuffmanEncoderDecoder {
	
	private Map<Character, Integer> frequencies;
	private Map<Character, String> encoder;
	private Map<String, Character> decoder;
	private Node structure;
	
	/**
	 * Constructs the Encoder/Decoder
	 * 
	 * @param frequencyData A string that is used to generate the frequency data of characters used
	 */

	public HuffmanEncoderDecoder(String frequencyData)
	{
		//maps each character to its frequency
		frequencies = new HashMap<Character, Integer>();
		
		//traverse the frequency data string
		for(int i=0; i<frequencyData.length();i++)
		{
			Character c = frequencyData.charAt(i);
			
			//if we haven't seen the character
			if (frequencies.get(c)==null)
			{
				//we put it in the frequencies hash map
				frequencies.put(c, 1);
			}
			//we have seen the character already
			else
			{
				//so update its frequency value in the hash map
				Integer frequency = frequencies.get(c);
				frequency++;
				frequencies.put(c, frequency);
			}
		}
		//creates the encoding/decoding values based on frequency data
		initializeHuffmanEncoderDecoder();
	}
	
	/**
	 * Creates the encoding and decoding based on the frequency data
	 * passed into the constructor
	 */
	
	public void initializeHuffmanEncoderDecoder()
	{
		/*
		 * Priority queue holds the nodes to be added to the tree based on the frequency
		 * of the character they hold or the order the were created if the frequency is the
		 * same.
		 */
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		//encoder - map characters to their encoded string
		encoder = new HashMap<Character,String>();
		//decoder - maps encoded strings to their appropriate character
		decoder = new HashMap<String,Character>();
		//id of the item added to the tree
		int id = 0;
		
		for(Character key: frequencies.keySet())
		{
			//wrap each character in a node and put the nodes in a priority queue
			pq.add(new Node(key,frequencies.get(key),id));
			id++;
		}
		
		//create the tree
		while(pq.size()>1)
		{
			Node left = pq.remove();
			Node right = pq.remove();
			//make a parent node that holds the first two nodes pulled from the pq
			Node parent = new Node(null, left.f+right.f, id);
			parent.left = left;
			parent.right = right;
			//put the parent node in the priority queue
			pq.add(parent);
			id++;
		}
		
		if (pq.size()!=0)
		{
			//pulls the root node out of the tree; structure
			//points to the root node
			structure = pq.remove();
		}
		
		if (structure != null)
		{
			//setUpMaps sets up a string encoding representation for each character
			setUpMaps(structure,"0");
		}
	}
	
	/**
	 * Recursively builds the string encoding for each character  
	 * 
	 * @param here The current node
	 * @param current The string representation of the character if we've reached a leaf node
	 */
	
	public void setUpMaps(Node here, String current)
	{
		//Make the appropriate strings for the right and left children if they exist
		String left = current.concat("0");
		String right = current.concat("1");
		
		//if we've reached a leaf we've reached a character so update the encoding/decoding
		//maps as appropriate
		if (here.left==null && here.right==null)
		{
			encoder.put(here.c, current);
			decoder.put(current, here.c);
			return;
		}
		//if there is a left child go down to it and look for more characters/leaves
		if (here.left!=null)
		{
			setUpMaps(here.left,left);
		}
		//do the same if there is right child
		if (here.right!=null)
		{
			setUpMaps(here.right,right);
		}		
	}
	
	/**
	 * Returns the frequency of each character in the string that was passed into the
	 * constructor
	 * 
	 * @param c the character the frequency is being checked for
	 * @return the frequency of that character
	 */
	public int getFrequencyForCharacter(char c){
		if (frequencies.get(c)==null)
		{
			return 0;
		}
		else 
		{
			return frequencies.get(c);
		}
	}
	
	/**
	 * Encodes a character
	 * 
	 * @param rawCharacter the character being encoded
	 * @return The encoding of that character or the character itself if it has no encoding
	 */
	public String encodeCharacter(char rawCharacter)
	{
		int frequency = getFrequencyForCharacter(rawCharacter);
		
		if (frequency==0)
		{
			return String.valueOf(rawCharacter);
		}
		else
		{
			return encoder.get(rawCharacter);
		}
	}
	
	/**
	 * Decodes a string of 0's and 1's
	 * 
	 * @param encodedCharacter The string of 0's and 1's
	 * @return the decoded character or if the passed in string was not 0's and 1's 0 unless it was a 
	 * single character and in that case returns the character
	 */
	public char decodeCharacter(String encodedCharacter){
		
		if (decoder.get(encodedCharacter)==null)
		{
			if (encodedCharacter.length()==1)
			{
				return encodedCharacter.charAt(0);
			}
			return 0;
		}
		else
		{
			return decoder.get(encodedCharacter);
		}
	}
	
	/**
	 * Encodes a string by encoding each indiviudal character
	 * 
	 * @param input The string to be encoded
	 * @return The encoding of that string
	 */
	public String encodeString(String input){
		
		String ret = "";
		
		for (int i=0; i<input.length(); i++)
		{
			ret = ret.concat(encodeCharacter(input.charAt(i)));
		}
		return ret;
	}
	
	/**
	 * Decodes a string by decoding each substring 
	 * 
	 * @param input The string of 0's and 1's
	 * @return The decoding of that string
	 */
	public String decodeString(String input){
		
		// i and j are pointers to the string and point to the beginning and ending of substrings respectively
		int i = 0;
		int j = 0;
		String ret = "";
		while (j < input.length()+1)
		{
			//if we reach a character in the input string is not a 0 or 1
			if (j >0 && input.charAt(j-1) != '1' && input.charAt(j-1)!='0')
			{
					//move the pointers past the invalid character
					i = j;
					//and return that character
					ret = ret.concat(String.valueOf(input.charAt(j-1)));
			}
			//if the decoder contains the substring return the approriate character
			if (decoder.containsKey(input.substring(i, j)))
			{
				ret = ret.concat(String.valueOf(decoder.get(input.substring(i,j))));
				//move the pointers past that substring
				i = j;
			}
			else j++;
		}
		return ret;
	}
	
	/**
	 * Represents a node in the Huffman tree
	 * 
	 * @author Jason Malia
	 */
	
	private static class Node implements Comparable
	{
		private Character c;
		private Integer f;
		private Node right;
		private Node left;
		private int id;
		
		/**
		 * Constructs a node that wraps a character or is part of the tree which holds
		 * characters 
		 * 
		 * @param c The character, or null if it does hold a character
		 * @param f The frequency of the character, or of its children
		 * @param id The order the node was added to the priority queue
		 */

		public Node(Character c, Integer f, int id)
		{
			this.c = c;
			this.f = f;
			this.id = id;
		}
		/**
		 * Compares nodes based on frequency and if the frequency is the same
		 * their 'id', the order they were added to the priority queue
		 * 
		 * @param node - The node being compared with.
		 */
		@Override
		public int compareTo(Object node) {
			
			Node tht;
			
			if (node instanceof Node)
			{
				tht = (Node)node;
			}
			else 
			{
				return 1;
			}	
			
			if (this.f.equals(tht.f))
			{
				return this.id - tht.id;
			}
			else
			{
				return this.f - tht.f;
			}
		}
	}
	
	/*
	public static void main(String args[])
	{
		
		HuffmanEncoderDecoder hed = new HuffmanEncoderDecoder("ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqurstuvwxyz.");
		
		String encoding = hed.encodeString("Jason is bored.");
		System.out.println("Encoding: "+encoding);
		String decoding = hed.decodeString(encoding);
		System.out.println("Decoding: "+decoding);
		
		HuffmanEncoderDecoder hed2 = new HuffmanEncoderDecoder("kkkkkkadsbbdacddb");
		String encoding1 = hed2.encodeString("kad");
		System.out.println("Encoding: "+encoding1);
		String decoding1 = hed2.decodeString("011Jas01011on");
		System.out.println("Decoding: "+decoding1);
	}
	*/
}
