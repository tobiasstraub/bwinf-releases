#include <iostream>
#include <vector>
#include <string>

#include "./../inc/Application.hpp"

auto main(int argc, char** argv) -> int
{
	try{
		std::locale::global(std::locale("German_germany"));

		std::vector<std::string> arguments;

		for (int i = 1; i < argc; ++i)
			arguments.push_back(argv[i]);

		Application a;
		return a.main(arguments);
	}
	catch (...) { // Unbehandelter Fehler
		std::cerr << "\n\nERROR - Unbehandelter Fehler aufgetreten.";
		return EXIT_FAILURE;
	}
}