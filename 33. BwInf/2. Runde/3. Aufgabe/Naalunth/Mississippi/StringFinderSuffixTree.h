#pragma once
#include "StringFinder.h"


struct SuffixTree;

typedef unsigned char uint8;

struct SuffixTreeNodeInternal;

//Suffix Tree Node
struct SuffixTreeNode
{
public:
	int begin = 0;	//Where in the string the label of the node begins
	uint8 isRoot : 1,
		isLeaf : 1,
		didGoToSibling : 1; //Used in the substring collection
	SuffixTreeNode* sibling = 0;

	~SuffixTreeNode();

	int EdgeLength(SuffixTree*);

	void AddChild(SuffixTreeNode*);
	void ReplaceChild(char label, SuffixTreeNode*, SuffixTree*);

	SuffixTreeNode* GetChild(char label, SuffixTree* parentTree);

	void Draw(int indent, SuffixTree*);

	SuffixTreeNodeInternal* thisInternal();

protected:
	SuffixTreeNode();
};


struct SuffixTreeNodeInternal : public SuffixTreeNode
{
public:
	int end = 0; //Where in the string the label of the node ends
	SuffixTreeNode* child = 0;
	SuffixTreeNode* suffixlink = 0; //suffix link used in tree construction

	SuffixTreeNodeInternal();
};


struct SuffixTreeNodeLeaf : public SuffixTreeNode
{
public:
	int labelOffset = 0; //Where the suffix from root to this leaf begins in the string

	SuffixTreeNodeLeaf();
};


struct SuffixTree
{
	SuffixTreeNode* root = 0;
	std::string* text = 0;

	SuffixTree(std::string*);
	~SuffixTree();

	//Extracts all Substrings matching the specifications
	map<PosLen, vector<int> > GetAllSubStrings(int minLength, int minAmount);

private:
	void BuildTree();
};



class StringFinderSuffixTree :
	public StringFinder
{
public:
	StringFinderSuffixTree();
	~StringFinderSuffixTree();
	map<PosLen, vector<int> > GetAllSubStrings(int l = 1, int k = 2);
protected:
	void OnStringChange(string* in);
private:
	SuffixTree* suffixTree_ = 0;
};
