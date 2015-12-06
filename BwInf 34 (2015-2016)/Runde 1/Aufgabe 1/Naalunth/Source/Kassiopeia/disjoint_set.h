#pragma once

#include <map>

/*
This is an implementation of the disjoint-set data structure according to https://en.wikipedia.org/wiki/Disjoint-set_data_structure.
T must be equal-comparable and copy-constructible.
*/
template<typename T>
class disjoint_set
{
private:
	struct set_node
	{
		T parent;
		std::size_t rank;
	};

	std::map<T, set_node> nodes;

public:
	disjoint_set() {}
	~disjoint_set() {}

	void insert(const T& elem)
	{
		if (nodes.find(elem) == nodes.end())
		{
			set_node new_node{ elem, size_t{0u} };
			nodes[elem] = new_node;
		}
	}

	T find(const T& node)
	{
		if (!(nodes[node].parent == node)) nodes[node].parent = find(nodes[node].parent);
		return nodes[node].parent;
	}

	//'union' cannot be used unfortunately
	void merge(const T& a, const T& b)
	{
		T a_root = find(a);
		T b_root = find(b);
		if (a_root == b_root) return;

		if (nodes[a].rank < nodes[b].rank)
			nodes[a_root].parent = b_root;
		else if (nodes[a].rank > nodes[b].rank)
			nodes[b_root].parent = a_root;
		else
		{
			nodes[b_root].parent = a_root;
			nodes[a_root].rank++;
		}
	}

};
