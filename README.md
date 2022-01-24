# Gitlet
Version-control system mimicing local features of git.

init
Usage: java gitlet.Main init
Description: Creates a new Gitlet version-control system in the current directory. This system will automatically start with one commit: a commit that contains no files and has the commit message initial commit (just like that, with no punctuation). It will have a single branch: master, which initially points to this initial commit, and master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates (this is called "The (Unix) Epoch", represented internally by the time 0.) Since the initial commit in all repositories created by Gitlet will have exactly the same content, it follows that all repositories will automatically share this commit (they will all have the same UID) and all commits in all repositories will trace back to it.
Runtime: Should be constant relative to any significant measure.
Failure cases: If there is already a Gitlet version-control system in the current directory, it should abort. It should NOT overwrite the existing system with a new one. Should print the error message A Gitlet version-control system already exists in the current directory.
Dangerous?: No
Our line count: ~25
add
Usage: java gitlet.Main add [file name]
Description: Adds a copy of the file as it currently exists to the staging area (see the description of the commit command). For this reason, adding a file is also called staging the file. Staging an already-staged file overwrites the previous entry in the staging area with the new contents. The staging area should be somewhere in .gitlet. If the current working version of the file is identical to the version in the current commit, do not stage it to be added, and remove it from the staging area if it is already there (as can happen when a file is changed, added, and then changed back). If the file had been marked to be removed (see gitlet rm), delete that mark.
Runtime: In the worst case, should run in linear time relative to the size of the file being added.
Failure cases: If the file does not exist, print the error message File does not exist. and exit without changing anything.
Dangerous?: No
Our line count: ~20
commit
Usage: java gitlet.Main commit [message]
Description: Saves a snapshot of certain files in the current commit and staging area so they can be restored at a later time, creating a new commit. The commit is said to be tracking the saved files. By default, each commit's snapshot of files will be exactly the same as its parent commit's snapshot of files; it will keep versions of files exactly as they are, and not update them. A commit will only update files it is tracking that have been staged at the time of commit, in which case the commit will now include the version of the file that was staged instead of the version it got from its parent. A commit will save and start tracking any files that were staged but weren't tracked by its parent. Finally, files tracked in the current commit may be untracked in the new commit as a result of the rm command (below).

The bottom line: By default a commit is the same as its parent. Staged and removed files are the updates to the commit.

Some additional points about commit:

The staging area is cleared after a commit.
The commit command never adds, changes, or removes files in the working directory (other than those in the .gitlet directory). The rm command will remove such files, as well as somehow marking them to be untracked by commit.
Any changes made to files after staging or removal are ignored by the commit command, which only modifies the contents of the .gitlet directory. For example, if you remove a tracked file using the Unix rm command (rather than Gitlet's command of the same name), it has no effect on the next commit, which will still contain the deleted version of the file.
After the commit command, the new commit is added as a new node in the commit tree.
The commit just made becomes the "current commit", and the head pointer now points to it. The previous head commit is this commit's parent commit.
Each commit should contain the date time it was made.
Each commit has a log message associated with it that describes the changes to the files in the commit. This is specified by the user. The entire message should take up only one entry in the array args that is passed to main. To include multiword messages, you'll have to surround them in quotes.
Each commit is identified by its SHA-1 id, which must include the file (blob) references of its files, parent reference, log message, and commit time.
Runtime: Runtime should be constant with respect to any measure of number of commits. Runtime must be no worse than linear with respect to the total size of files the commit is tracking. Additionally, this command has a memory requirement: Committing must increase the size of the .gitlet directory by no more than the total size of the staged files at the time of commit, not including additional metadata. This means don't store redundant copies of versions of files that a commit receives from its parent. You are allowed to save whole additional copies of files; don't worry about only saving diffs, or anything like that.
Failure cases: If no files have been staged (or marked for untracking: more on that next), aborts. Print the message No changes added to the commit. Every commit must have a non-blank message. If it doesn't, print the error message Please enter a commit message. It is not a failure for tracked files to be missing from the working directory or changed in the working directory. Just ignore everything outside the .gitlet directory entirely.
Dangerous?: No
Differences from real git: In real git, commits may have multiple parents (due to merging) and also have considerably more metadata.
Our line count: ~35
Here's a picture of before-and-after commit:

Before and after commit

rm
Usage: java gitlet.Main rm [file name]
Description: Unstage the file if it is currently staged. If the file is tracked in the current commit, mark it to indicate that it is not to be included in the next commit (presumably you would store this mark somewhere in the .gitlet directory), and remove the file from the working directory if the user has not already done so (do not remove it unless it is tracked in the current commit).
Runtime: Should run in constant time relative to any significant measure.
Failure cases: If the file is neither staged nor tracked by the head commit, print the error message No reason to remove the file.
Dangerous?: Yes (although if you use our utility methods, you will only hurt your repository files, and not all the other files in your directory.)
Our line count: ~20
log
Usage: java gitlet.Main log
Description: Starting at the current head commit, display information about each commit backwards along the commit tree until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits. (In regular Git, this is what you get with git log --first-parent). This set of commit nodes is called the commit's history. For every node in this history, the information it should display is the commit id, the time the commit was made, and the commit message. Here is an example of the exact format it should follow:

   ===
   commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
   Date: Thu Nov 9 20:00:05 2017 -0800
   A commit message.

   ===
   commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
   Date: Thu Nov 9 17:01:33 2017 -0800
   Another commit message.

   ===
   commit e881c9575d180a215d1a636545b8fd9abfb1d2bb
   Date: Wed Dec 31 16:00:00 1969 -0800
   initial commit
There is a === before each commit and an empty line after it. As in real Git, each entry displays the unique SHA-1 id of the commit object. The timestamps displayed in the commits reflect the current timezone, not UTC; as a result, the timestamp for the initial commit does not read Thursday, January 1st, 1970, 00:00:00, but rather the equivalent Pacific Standard Time. Display commits with the most recent at the top. By the way, you'll find that the Java classes java.util.Date and java.util.Formatter are useful for getting and formatting times. Look into them instead of trying to construct it manually yourself!

For merge commits (those that have two parent commits), add a line just below the first, as in

   ===
   commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
   Merge: 4975af1 2c1ead1
   Date: Sat Nov 11 12:30:00 2017 -0800
   Merged development into master.
where the two hexadecimal numerals following "Merge:" consist of the first seven digits of the first and second parents' commit ids, in that order. The first parent is the branch you were on when you did the merge; the second is that of the merged-in branch. This is as in regular Git.

Runtime: Should be linear with respect to the number of nodes in head's history.
Failure cases: None
Dangerous?: No
Our line count: ~20
Here's a picture of the history of a particular commit. If the current branch's head pointer happened to be pointing to that commit, log would print out information about the circled commits:

History

The history ignores other branches and the future. Now that we have the concept of history, let's refine what we said earlier about the commit tree being immutable. It is immutable precisely in the sense that the history of a commit with a particular id may never change, ever. If you think of the commit tree as nothing more than a collection of histories, then what we're really saying is that each history is immutable.

global-log
Usage: java gitlet.Main global-log
Description: Like log, except displays information about all commits ever made. The order of the commits does not matter.
Runtime: Linear with respect to the number of commits ever made.
Failure cases: None
Dangerous?: No
Our line count: ~10
find
Usage: java gitlet.Main find [commit message]
Description: Prints out the ids of all commits that have the given commit message, one per line. If there are multiple such commits, it prints the ids out on separate lines. The commit message is a single operand; to indicate a multiword message, put the operand in quotation marks, as for the commit command below.
Runtime: Should be linear relative to the number of commits.
Failure cases: If no such commit exists, prints the error message Found no commit with that message.
Dangerous?: No
Differences from real git: Doesn't exist in real git. Similar effects can be achieved by grepping the output of log.
Our line count: ~15
status
Usage: java gitlet.Main status
Description: Displays what branches currently exist, and marks the current branch with a *. Also displays what files have been staged or marked for untracking. An example of the exact format it should follow is as follows.

=== Branches ===
*master
other-branch

=== Staged Files ===
wug.txt
wug2.txt

=== Removed Files ===
goodbye.txt

=== Modifications Not Staged For Commit ===
junk.txt (deleted)
wug3.txt (modified)

=== Untracked Files ===
random.stuff
There is an empty line between each section. Entries should be listed in lexicographic order, using the Java string-comparison order (the asterisk doesn't count). A file in the working directory is "modified but not staged" if it is

Tracked in the current commit, changed in the working directory, but not staged; or
Staged for addition, but with different contents than in the working directory; or
Staged for addition, but deleted in the working directory; or
Not staged for removal, but tracked in the current commit and deleted from the working directory.

The final category ("Untracked Files") is for files present in the working directory but neither staged for addition nor tracked. This includes files that have been staged for removal, but then re-added without Gitlet's knowledge. Ignore any subdirectories that may have been introduced, since Gitlet does not deal with them.

The last two sections (modifications not staged and untracked files) are extra credit. Feel free to leave them blank (leaving just the headers).

Runtime: Make sure this depends only on the amount of data in the working directory plus the number of files staged to be added or deleted plus the number of branches.
Failure cases: None
Dangerous?: No
Our line count: ~45
checkout
Checkout is a kind of general command that can do a few different things depending on what its arguments are. There are 3 possible use cases. In each section below, you'll see 3 bullet points. Each corresponds to the respective usage of checkout.

Usages:

java gitlet.Main checkout -- [file name]
java gitlet.Main checkout [commit id] -- [file name]
java gitlet.Main checkout [branch name]
Descriptions:

Takes the version of the file as it exists in the head commit, the front of the current branch, and puts it in the working directory, overwriting the version of the file that's already there if there is one. The new version of the file is not staged.
Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory, overwriting the version of the file that's already there if there is one. The new version of the file is not staged.
Takes all files in the commit at the head of the given branch, and puts them in the working directory, overwriting the versions of the files that are already there if they exist. Also, at the end of this command, the given branch will now be considered the current branch (HEAD). Any files that are tracked in the current branch but are not present in the checked-out branch are deleted. The staging area is cleared, unless the checked-out branch is the current branch (see Failure cases below).
Runtimes:

Should be linear relative to the size of the file being checked out.
Should be linear relative to the size of the file being checked out.
Should be linear with respect to the total size of the files in the commit's snapshot. Should be constant with respect to any measure involving number of commits. Should be constant with respect to the number of branches.
Failure cases:

If the file does not exist in the previous commit, aborts, printing the error message File does not exist in that commit.
If no commit with the given id exists, print No commit with that id exists. Else, if the file does not exist in the given commit, print the same message as for failure case 1.
If no branch with that name exists, print No such branch exists. If that branch is the current branch, print No need to checkout the current branch. If a working file is untracked in the current branch and would be overwritten by the checkout, print There is an untracked file in the way; delete it or add it first. and exit; perform this check before doing anything else.
Differences from real git: Real git does not clear the staging area and stages the file that is checked out. Also, it won't do a checkout that would overwrite or undo changes (additions or removals) that you have staged.
A [commit id] is, as described earlier, a hexadecimal numeral. A convenient feature of real Git is that one can abbreviate commits with a unique prefix. For example, one might abbreviate

a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
as

a0da1e
in the (likely) event that no other object exists with a SHA-1 identifier that starts with the same six digits. You should arrange for the same thing to happen for commit ids that contain fewer than 40 characters. Unfortunately, using shortened ids might slow down the finding of objects if implemented naively (making the time to find a file linear in the number of objects), so we won't worry about timing for commands that use shortened ids. We suggest, however, that you poke around in a .git directory (specifically, .git/objects) and see how it manages to speed up its search. You will perhaps recognize a familiar data structure implemented with the file system rather than pointers.

Only version 3 (checkout of a full branch) modifies the staging area: otherwise files scheduled for addition or removal remain so.

Dangerous?: Yes!
Our line counts:

~15
~5
~15
branch
Usage: java gitlet.Main branch [branch name]
Description: Creates a new branch with the given name, and points it at the current head node. A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node. This command does NOT immediately switch to the newly created branch (just as in real Git). Before you ever call branch, your code should be running with a default branch called "master".
Runtime: Should be constant relative to any significant measure.
Failure cases: If a branch with the given name already exists, print the error message A branch with that name already exists.
Dangerous?: No
Our line count: ~10
All right, let's see what branch does in detail. Suppose our state looks like this:

Simple history

Now we call java gitlet.Main branch cool-beans. Then we get this:

Just called branch

Hmm... nothing much happened. Let's switch to the branch with java Gitlet checkout cool-beans:

Just switched branch

Nothing much happened again?! Okay, say we make a commit now. Modify some files, then java gitlet.Main add... then java gitlet.Main commit...

Commit on branch

I was told there would be branching. But all I see is a straight line. What's going on? Maybe I should go back to my other branch with java Gitlet checkout master:

Checkout master

Now I make a commit...

Branched

Phew! So that's the whole idea of branching. Did you catch what's going on? All that creating a branch does is to give us a new pointer. At any given time, one of these pointers is considered the currently active pointer, or the head pointer (indicated by *). We can switch the currently active head pointer with checkout [branch name]. Whenever we commit, it means we add a new commit in front of the currently active head pointer, even if one is already there. This naturally creates branching behavior.

Make sure that the behavior of your branch, checkout, and commit match what we've described above. This is pretty core functionality of Gitlet that many other commands will depend upon. If any of this core functionality is broken, very many of our autograder tests won't work!

rm-branch
Usage: java gitlet.Main rm-branch [branch name]
Description: Deletes the branch with the given name. This only means to delete the pointer associated with the branch; it does not mean to delete all commits that were created under the branch, or anything like that.
Runtime: Should be constant relative to any significant measure.
Failure cases: If a branch with the given name does not exist, aborts. Print the error message A branch with that name does not exist. If you try to remove the branch you're currently on, aborts, printing the error message Cannot remove the current branch.
Dangerous?: No
Our line count: ~15
reset
Usage: java gitlet.Main reset [commit id]
Description: Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit. Also moves the current branch's head to that commit node. See the intro for an example of what happens to the head pointer after using reset. The [commit id] may be abbreviated as for checkout. The staging area is cleared. The command is essentially checkout of an arbitrary commit that also changes the current branch head.
Runtime: Should be linear with respect to the total size of files tracked by the given commit's snapshot. Should be constant with respect to any measure involving number of commits.
Failure case: If no commit with the given id exists, print No commit with that id exists. If a working file is untracked in the current branch and would be overwritten by the reset, print There is an untracked file in the way; delete it or add it first. and exit; perform this check before doing anything else.
Dangerous?: Yes!
Differences from real git: This command is closest to using the --hard option, as in git reset --hard [commit hash].
Our line count: ~10
merge
Usage: java gitlet.Main merge [branch name]
Description: Merges files from the given branch into the current branch. This method is a bit complicated, so here's a more detailed description:

First consider what might be called the split point of the current branch and the given branch. For example, if master is the current branch and branch is the given branch: Split point The split point is a latest common ancestor of the current and given branch heads:

A common ancestor is a commit to which there is a path (of 0 or more parent pointers) from both branch heads.
A latest common ancestor is a common ancestor that is not an ancestor of any other common ancestor. For example, although the leftmost commit in the diagram above is a common ancestor of master and branch, it is also an ancestor of the commit immediately to its right, so it is not a latest common ancestor. If the split point is the same commit as the given branch, then we do nothing; the merge is complete, and the operation ends with the message Given branch is an ancestor of the current branch. If the split point is the current branch, then the current branch is set to the same commit as the given branch and the operation ends after printing the message Current branch fast-forwarded. Otherwise, we continue with the steps below.
Any files that have been modified in the given branch since the split point, but not modified in the current branch since the split point should be changed to their versions in the given branch (checked out from the commit at the front of the given branch). These files should then all be automatically staged. To clarify, if a file is "modified in the given branch since the split point" this means the version of the file as it exists in the commit at the front of the given branch has different content from the version of the file at the split point.
Any files that have been modified in the current branch but not in the given branch since the split point should stay as they are.
Any files that have been modified in both the current and given branch in the same way (i.e., both to files with the same content or both removed) are left unchanged by the merge. If a file is removed in both, but a file of that name is present in the working directory that file is not removed from the working directory (but it continues to be absent—not staged—in the merge).
Any files that were not present at the split point and are present only in the current branch should remain as they are.
Any files that were not present at the split point and are present only in the given branch should be checked out and staged.
Any files present at the split point, unmodified in the current branch, and absent in the given branch should be removed (and untracked).
Any files present at the split point, unmodified in the given branch, and absent in the current branch should remain absent.
Any files modified in different ways in the current and given branches are in conflict. "Modified in different ways" can mean that the contents of both are changed and different from other, or the contents of one are changed and the other file is deleted, or the file was absent at the split point and has different contents in the given and current branches. In this case, replace the contents of the conflicted file with

<<<<<<< HEAD
contents of file in current branch
=======
contents of file in given branch
>>>>>>>
(replacing "contents of..." with the indicated file's contents) and stage the result. Treat a deleted file in a branch as an empty file. Use straight concatenation here. In the case of a file with no newline at the end, you might well end up with something like this:

<<<<<<< HEAD
contents of file in current branch=======
contents of file in given branch>>>>>>>
This is fine; people who produce non-standard, pathological files because they don't know the difference between a line terminator and a line separator deserve what they get.

Once files have been updated according to the above, and the split point was not the current branch or the given branch, merge automatically commits with the log message Merged [given branch name] into [current branch name]. Then, if the merge encountered a conflict, print the message Encountered a merge conflict. on the terminal (not the log). Merge commits differ from other commits: they record as parents both the head of the current branch (called the first parent) and the head of the branch given on the command line to be merged in.
There is one complication in the definition of the split point. You may have noticed that we referred to "a", rather than "the" latest common ancestor. This is because there can be more than one in the case of "criss-cross merges", such as this: Criss-Cross Merge Here, the solid lines are first parents and the dashed lines are the merged-in parents. Both the commits labeled "A" and "B" above are latest common ancestors. You might want to think about why it can make a difference which gets used as the split point. We'll use the following rule to choose which of multiple possible split points to use:

Choose the candidate split point that is closest to the head of the current branch (that is, is reachable by following the fewest parent pointers along some path).
If multiple candidates are at the same closest distance, choose any one of them as the split point. (We will make sure that this only happens in our test cases when the resulting merge commit is the same with any of the closest choices.)
By the way, we hope you've noticed that the set of commits has progressed from a simple sequence to a tree and now, finally, to a full directed acyclic graph.

Runtime: O(NlgN+D), where N is the total number of ancestor commits for the two branches and D is the total amount of data in all the files under these commits.
Failure cases: If there are staged additions or removals present, print the error message You have uncommitted changes. and exit. If a branch with the given name does not exist, print the error message A branch with that name does not exist. If attempting to merge a branch with itself, print the error message Cannot merge a branch with itself. If merge would generate an error because the commit that it does has no changes in it, just let the normal commit error message for this go through. If an untracked file in the current commit would be overwritten or deleted by the merge, print There is an untracked file in the way; delete it or add it first. and exit; perform this check before doing anything else.
Dangerous?: Yes!
Differences from real git: Real Git does a more subtle job of merging files, displaying conflicts only in places where both files have changed since the split point.

Real Git has a different way to decide which of multiple possible split points to use.

Real Git will force the user to resolve the merge conflicts before committing to complete the merge. Gitlet just commits the merge, conflicts and all, so that you must use a separate commit to resolve problems.

Real Git will complain if there are unstaged changes to a file that would be changed by a merge. You may do so as well if you want, but we will not test that case.

Our line count: ~70
