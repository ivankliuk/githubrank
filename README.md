GithubRank
==========

Summary
-------

GitHub portal is centered around organizations and repositories. Each organization has
many repositories and each repository has many contributors. GithubRank application provides
endpoint that given the name of the organization will return a list of contributors sorted by the
number of contributions.

Installation
------------

You need to have `git` and `sbt` installed on your OS.

```bash
$ git clone git@github.com:ivankliuk/githubrank.git && cd githubrank
```

(Optional) Obtain Github API token as described
[here](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
and set `GH_TOKEN` environment variable:

```bash
$ export GH_TOKEN=Github_API_token
```

Running
-------

To run the application locally:

```bash
$ sbt runProd
```

It will be listening on a socket address `localhost:8080`.

Example
-------

Sample input:

```bash
$ time curl -i http://localhost:8080/org/scala/contributors
```

will return something like:

```json
[
    {
        "contributions": 3154,
        "login": "lrytz"
    },
    {
        "contributions": 4145,
        "login": "retronym"
    },
    {
        "contributions": 4540,
        "login": "adriaanm"
    },
    {
        "contributions": 4689,
        "login": "paulp"
    },
    {
        "contributions": 5969,
        "login": "SethTisue"
    }
]
```

TODO
----

* Tests
* Logging
* Circuit breaker for `WSClient`
* Streaming endpoint
* Throttling

