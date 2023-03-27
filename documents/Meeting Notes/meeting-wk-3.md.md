# Meeting Notes

Note taker: Michelle Chao Chen
Scrum master: Michelle Chao Chen
Date: Thursday 25th November 2021

## Tasks for Sprint

> All tasks completed by time of the meeting are ticked 

- [x] Finalise the requirements and convert them to gitlab issues
- [x] Create a database schema of submission
- [x] Push template project to Gitlab
- [ ] Add functional requirements to Gitlab
- [x] Determine bounded contexts
- [ ] Complete Assigmment 1 task 1 on Overleaf (Sunday) 
- [ ] Create UML Component Diagram 
- [ ] Create shared package of entity (Reference Schema on Lucidchart)
- [ ] Add weights to issues on Gitlab
- [ ] Divide groups for microservices and split issues within microservices
- [ ] Name of branch will be name of issues
- [x] Add internal deadline subtasks
- [x] Create boards for tracking which issues are being done or in progress - indicate the progress. 

## Questions for TA
* Docker container for the submission? 
* Eureka server for microservices? 
* Microservices can be different packages within a module, or we should start all of them independently?
* Are the courses in the system created by lecturers or can we assume they are pre-determined? 
* How are the databases for different microservices separated? 

## Discussion

* Some modules may not need all the entities. Take this into consideration when creating a shared module of entities for each of the microservices. 
* Hard code ports for microservices - we do not need to use eureka server. 
* Make sure that micro services are stateless. Have same view for all microservices. Maximise instructions run on one microservices. This leverages the advantage of using microservices. 
* Keep courses pre determined. Lay out the assumptions of the system during the final presentation. 
* Microservice X does not access data of Microservice Y0
* Remove won't haves from the gitlab issues since they will not be implemented anyway.
* Use milestones to indicate that issues are parts of a sprint 
* Weight is how important an issue is, with timing the issues - that's for measuring how much work is done by each group member. 
* Make sure high weight issues are done by the early sprints, since weight indicates the importance of an issue. 
* We are assessed on sprint management and implementation. Specific components that we are assessed on are outlined below
* **Sprint management**: how the tasks are defined, how we prioritize tasks, how we improve, responsiveness to feedback. 
* **Implementation**: object design (software principles applied), division of responsibility between microservices. How the methods are written. Code Style - clear names and reading the code should make it clear what it is doing.
* **Testing**: test coverage > 80%. Integration testing between microservices. Functional testing - testing entire functionalities.  
* **Tooling**: how gitlab is used. Running the project shouldn't have the need to re build gradle each time. Code reviews are assessed, should be done thoroughly on gitlab. 