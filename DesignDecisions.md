# Design decisions

This document provides some explanations about my thoughts and decisions
regarding how and why I decided to implement TomokiDB-Web in this way.

## Why make it a web-app?

The answer is quite simple. I initially tried to create a desktop app with GUI using JavaFX,
but didn't get very far, because I constantly ran into difficulties.
Since I have more experience with web-frontend-technologies and since additional practice
with these would likely be of value for my current job, I decided to write a (locally run) web-app.

## Why use Apache Derby over a different database system?

I want this app to be comfortable to use and set up, which means that it should have
as few requirements for execution on a user's device as possible.
Because of this, I went with an embedded database system, which does not require users
to install a database system and keep it running themselves.

I chose Apache Derby over the apparently more popular SQLite because it seems to be easier to
integrate into the rest of my application (Java, JPA).

## Why use CSR instead of SSR?

Client-side rendering seems to be the norm in modern web-development and it also allows me
to practice creating REST-APIs and make use of Vue.js, which, again, is useful for my job.
