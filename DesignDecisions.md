# Design decisions

This document provides some explanations about my thoughts and decisions
regarding how and why I decided to implement TomokiDB-Web in this way,
both as a reminder for myself and for whoever is interested.

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

## Why not use JSON Schema for advanced validation?

At some point I sought for a way to validate the JSON that this project's backend consumes
in ways that were advanced enough that they seemed to go beyond the capabilities of the
Javax validation constraints and the Fasterxml Jackson annotations.

On my search I came across [JSON Schema](https://json-schema.org/), which was quite intriguing
and seemed perfectly suitable for my needs.
Halfway through my implementation with this approach I realised that things were getting a bit too
complicated, so I instead opted to validate and manipulate the models generated from the
JSON with Java code, even if that may be a less performant and clean solution.