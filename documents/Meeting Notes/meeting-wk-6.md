# Meeting 16 December

Note taker: Michelle Chao Chen\
Scrum master: Koen Snijder\
Date: Thursday 16 December 2021

## Tasks

- [ ] Sprint retrospective of **Sprint 3** is missing
- [ ] Reformat the sprint retrospective so that the **reflection** makes it clear what went wrong and what you changed. 
- [ ] Add time **estimate and spent** on gitlab issues.
- [ ] Add **definition of done** on issues.
- [ ] **Test coverage criteria** does nothing, no indication of test coverage since nothing has been configured.

## Summary of last sprint

- Made tests for each service
- Andrew has been changing services, he started to refactoring for code to be consistent
- Reformatted the error handling to use controller advice
- The document has been finished on thursday morning

## Feedback from George

- Have half the should haves done before the break. 
- You never know how complicated the issues are until you start, so still get started with the issues
- Make it explicitly clear what it means for an issue is finished. Have a clear definition of done
- Format the reflections -- it is difficult to interpret. Make it clear what went wrong and write what you did to fix it. This allows checking whether you are doing it
- You are responsive to feedback. Whether or not apply the feedback to code that is not seen. You need to proactive to get a high grade
- Design patterns are great. They show necessary and understanding. 
- A source folder that is empty. It is template code, get rid of it. 
- Naming schemes are confusing or detrimental. Application Service is a confusing name. Find another name for the TA application. Think about what some
- Some methods are quite large. Create application is too large. (make a method to extract )
- Too many exceptions in one method. Maybe don't throw the exceptions 
- Make sure that there is a method that could possibly catch the errors. 
- Tests doesn't give a coverage criteria. Place a coverage criteria in the README. 
- Get a way to see how much coverage you have. Focus on branch coverage to see how much more tests you need to have
- Integration tests are not present: testing one microservice/class by mocking the environment around it. 
- Mock all interactions between all microservices. Service A sends alpha to B. Serivce B sends pheta to A, then the tests for A should mock pheta. 
- Place it in a integration test folder. Then the division of tests is more clear. Or add `integrationTest` to the test method signature
- Tooling is good. Have that two people review and approve for every merge request. 
- For the merge from dev to main, then everyone should be placed as reviewer. 

