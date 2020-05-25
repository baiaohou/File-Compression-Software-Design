# File Compression Software Design

### Author: Leo Baiao Hou ***(Implemented in Java)***

To compress a file, the **Huffman coding algorithm** should be applied. To begin with, count how many times every character occurs in a file. These counts are used to build weighted nodes that will be leaves in the Huffman tree. The word character is used, but we mean 8-bit chunk and this chunk-size could change. From these counts build the Huffman tree. First create one node per character, weighted with the number of times the character occurs, and insert each node into a priority queue. Then choose two minimal nodes, join these nodes together as children of a newly created node, and insert the newly created node into the priority queue. The new node is weighted with the sum of the two minimal nodes taken from the priority queue. Continue this process until only one node is left in the priority queue. This is the root of the Huffman tree. 

## Overview

Then create a table or map of characters (8-bit chunks) to codings. The table of encodings is formed by traversing the path from the root of the Huffman tree to each leaf, each root-to-leaf path creates an encoding for the value stored in the leaf. When going left in the tree append a zero to the path; when going right append a one. All characters/encoding bit pairs may be stored in some kind of table or map to facilitate easy retrieval later. The table should be of the appropriate size (roughly 256). Finally, read the input file a second time. For each character/8-bit chunk read, write the encoding of the character (obtained from the map of encodings) to the compressed file.

To uncompress the file later, it is essential to recreate the same Huffman tree that was used to compress (so the codes you send will match). This tree will be stored directly in the compressed file (e.g., using a preorder traversal), or it might be created from character counts stored in the compressed file. In either case, this information must be coded and transmitted along with the compressed data (the tree/count data will be stored first in the compressed file, to be read during uncompression. There's more information below on storing/reading information to re-create the tree.

## Storing the Huffman Tree

For decompression to work with Huffman coding, information must be stored in the compressed file which allows the Huffman tree to be re-created so that decompression can take place. There are many options here. You can store all codes and lengths as normal (32 bit) int values or you can try to be inventive and save space. For example, it is possible to store just chunk/character counts and recreate the codes from the counts (i.e., store 256 counts, one for each 8-bit character). It's also possible to store code-lengths and codes using bit-at-a-time operations. Any solution to storing information in the compressed file is acceptable, but full credit requires some attempt to save space/storage. Space-saving techniques are defined as those using less space than simply storing 256 counts as 32 bit ints. One useful technique is to write the tree to the file using a preorder traversal. You must use this technique for this assignment. You can use a 0 or 1 bit to differentiate between internal nodes and leaves, for example. The leaves must store character values (in the general case using 9-bits because of the pseudo-eof character).

For example, the sequence of 0's and 1's below represents the tree below (if you write the 0's and 1's the spaces wouldn't appear, the spaces are only to make the bits more readable to humans.)

```0 1 001000001 1 000100000 1 001010100```

The first `0` indicates a non-leaf, the second `0` is the left child of the root, a non-leaf. The next `1` is a leaf, it is followed by 9 bits that represent 65 (`001000001` is 65 in binary), the ASCII code for 'A'. Then there's a `1` for the right child of the left child of the root, it stores 32 (`000100000` is 32 in binary), the ASCII value of a space. The next `1` indicates the right child of the root is a leaf, it stores the ASCII value for a 'T' which is 84 (`001010100` is 84 in binary).

## Forcing Compression

If compressing a file results in a file larger than the file being compressed (this is always possible) then no compressed file should be created and the write method should return the expected size of the compressed file. 

If the user forces compression using the 'force' argument, then compression occurs even if the compressed file is bigger.

 

To determine if compression results in a smaller file, you'll need to calculate the number of characters/chunks in the original file (your program will compute this by determining character/chunk counts). The size of the compressed file can be calculated from the same counts using the size of each character's encoded number of bits. You must also remember to calculate the file-header information stored in the compressed program. To be more precise, if there are 52 A's, and each A requires 4 bits to encode, then the A's contribute 52*4 = 108 bits to the compressed file. You'll need to make calculations like this for all characters.

The `IHuffHeader` interface specifies a method `headerSize` to help with keeping the code for headers in one place. `Huff.java` will implement the methods from the `IHuffHeader` interface.
