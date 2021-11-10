# Gitlet Design Document

**Name**: Aniketh Prasad 

# Classes and Data Structures
##Database
This class manages all git methods such as init, add, status, commit, etc. as well as the branches.

**Fields**
1. `StagingArea stage`: StagingArea holding all files ready to be committed.
##StagingArea
This class stores all the files which are in the staging area.

**Fields**
1. `Hashmap<String, String> staged`: Files which have been git added 
##Commit
This class stores all the data in a commit, including the commit message and previous commit in the branch.

**Fields**
1. `String hash`: String containing the hash of the current commit
2. `String prevHash`: String containing hash of previous commit in the branch
3. `String m`: String containing commit message
4. `Hashmap<String, String> blob`: Hashmap mapping filename to hash for the file containing blob of the current commit
##Utilities
Utilities file consisting of static methods to aid with repetitive file reading tasks.
# Algorithms
##Main Class
* Uses a switch case to identify which git command is called and then validates that the correct amount of parameters
are provided. Then once the command is identified, it calls the corresponding method in the Database where the function
will get carried out.

##Commit Class
* `String prevHash`: instance variable storing the previous hash in the branch. This hash can be recursively accessed to go backwards in the commit log.
The hash can then be used with `Hashmap<String, String> blob` to access the file associated with the hash.

# Persistence

In gitlet we want to save the data in each commit as blobs in .txt files. 

* We will write all the commit data into a file and store its hash and filename. This file is saved within the .gitlet folder in the current directory 
whereever `git init` was called.