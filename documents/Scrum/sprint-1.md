# Sprint 1 (Week 3 to 4)

Scrum master: Michelle Chao Chen

## Tasks Completed

1. **Completed Assignment 1 Task 1**. We outlined the responsibilities of each microservice so that we know which endpoints should be implemented by which service
2. **Divided teams to work on microservices**. We divided the group into subgroups with either 1 or 2 people to work on a microservice. Each team was responsible for creating the skeleton of their microservice and possibly one endpoint. 
3. **Edit issues to give some implementation details**. Each member is responsible for doing this for the issues they have been assigned to. 
4. **Created an entity module** that specifies how the entities are designed. 
5. **Created boards to track the progress of issues** (state: backlog, scheduled, in progress, testing, completed)
6. **Created milestones** to make it clear which issues are part of this sprint.
7. **Created documents directory**. This directory will contain all documents created during the project. It also has sub directories to track agendas, meeting notes, scrum, and assignments. 


## Meetings

> We had two other meetings over the course of this sprint for discussion on tasks we should do and agree on implementation

### Meeting on 23rd November

- [x] Finalise the schema for the database
- [x] Create a draft for UML Component Diagram
- [x] Create final version of UML Component Diagram - Ziyar
- [x] Write bounded context descriptions (Assignment 1 Task 1) - Michelle

### Meeting on 28th November

We tasks we completed or discusioo

- [x] Complete assignment 1 task 1
- [ ] **Decide on design pattern for task 2 (so we can start implementing maybe not have everything written)** - We had not completely decided on the design pattern to use within each microservice.
- [x] Decide on issues for this sprint
- [x] Create shared module for entities
- [x] Add weights to issues 
- [x] Assign a Sprint master - Nicolas
- [x] Create groups for each microsrvice

## Reflection


## Main Problem Encountered

### Problem 1 

**Description:**
The requirements were all formatted as user stories which is not satisfactory because they can't be translated directly into a task. Moreover, they are too general and encapsulate too much functionality. Moreover, they should be modular enough such that the name of a branch can be the same as the issue.\

**Solution:** Reformatted all issues by breaking down each user story into 3 to 4 requirements that translate to tasks that can be implemented in code. The requirements were reformatted according to the "Requirements Tetris" document on brightspace. All requirements were translated to gitlab issues, with shorter titles and the more elaborate descriptions are in the description section.

### Problem 2

**Description:**
Tooling on Gitlab needs to be improved: weights and time estimates need to be added to issues so that we can keep track of the workload of each memeber. Moreover, we should create boards for the issues so that we know the progress (backlog, scheduled, in progress, testing, completed) for each issue. This gives everyone a good overview. Moreover, we should use milestones to indicate the sprint each issue belongs to.\ 

**Solution:** We made sure to edit our own issues so that everything has a weight and time estimate. We also created labels to track the progress, these labels indicating progress are also the labels that separates boards. Moreover, milestones were created for each sprint. All these new tooling were integrated with issues. 


### Problem 3

**Description:**
Changes should be on `main` before Wednesday afternoon and the sprint summary should also be send by this time. This is what course staff run gitinspector on.  

**Solution:** Make sure to integrate all changes frequently into `development` and have a merge request into main before Wednesday noon into `main`. This would only be possible if all development is complete by Tuesday evening. 

## Adjustments For Next 

* Create modular issues
* Use all tooling (boards, time estimate, weight, milestone) for all issues created
* Make modular commits
* Assign issues to members at the beginning of the sprint so that they are completed early
* Merge into `main` latest by Wednesday noon before the course staff runs tools on the repo to check progress