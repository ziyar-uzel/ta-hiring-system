# Sprint 4 (Week 6 to 7)

Scrum master: Koen Snijder

## Tasks Completed

1. **Completed Assignment 1**. We added the documentation and of the design patterns and the design pattern class diagrams.
2. **Reformatted exception handling**. We reformatted the exceptions to be more clean.
3. **Added more tests**. Every microservice has at least some unit/integration tests now.
4. **Reformatted code for consistency**. We changed quite some code so our codebase is more consistent.
5. **Added/implemented should/could have features**. We made progress on adding as much as should and could haves as possible.
6. **Updated gitlab descriptions**. We updated our gitlab descriptions of our issues to have a clear definition of done and be more concise overall.

## Meetings

> We had two other meetings over the course of this sprint for discussion on tasks we should do and agree on implementation and one meeting to prepare the general rehearsal of our presentation

### Meeting on 16th December

This meeting was to update our TA on how far we are, but mainly to get feedback on the code we had made so far.
####Obtained feedback about our code

- [x] Delete some redundant folders
- [x] Some methods are too large, they should be split up
- [x] Some methods throw too many exceptions
- Get a way to show the test coverage and update the README on how to do this
- Add integration tests to test interaction between microservices
- [x] Some naming schemes are quite confusing and should be clearer

####Other feedback

- [x] Update the gitlab issues to have a better definition of done
- [x] Reformat your reflection in the sprint-retrospective. It should be more concise and clear
- [x] Everyone should check out the merge to main that is once a week

### Meeting on 19th December

####Tasks

- [x] Divide the should and could haves
- [x] Plan a meeting to create a presentation for the general rehearsal
- [x] Decide on when we should start working on assignment 2
- [x] Discussed again the reformatting of the sprint retrospective and of the gitlab issues
- [x] Discussed that the application microservice should be renamed to submission microservice for clariy
####Points of feedback 

- [x] Only merge to development, except for documents, they can go to main directly. We got into quite some issues because some code was merged to main
- [x] Run the checkstyle locally, not everyone was doing this for every request they made

###Meeting on 20th December

This meeting was to create and rehearse a presentation before the general rehearsal of the OP

## Reflection

**Things learned:**
* A bunch of things that can be improved about our code:
  * Delete redundant code, files and folders to reduce cluttering
  * Be careful of large methods and methods that throw a lot of exceptions
  * Naming should be consistent and clear, inconsistencies and confusing names should be changed
  * Make sure to reach the required amount of coverage and make sure to update the README on how to create a report for this if necessary
* Gitlab issues should have a clear definition of done in their descriptions
* The reflection in the sprint-retrospective should be clear and consice
* The weekly merge to main should be checked out by everyone\
\
**Things to keep in mind for next weeks/projects:** 
* Code should be merged to DEVELOPMENT. Only documents can go directly into main
* Checkstyle should be run locally. If that is not done, the person who hasn't done it should fix it
* Last week before Christmas is a difficult week to work in. People are tired and have started making other plans. Luckily, our Must Haves were already done, but we still had too many expectations for this week.