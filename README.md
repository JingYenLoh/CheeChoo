# ST0316 ADVANCED JAVA PROGRAMMING

Requirements
================================

DEADLINE: &lt;2017-08-07 Mon&gt;

Basic
-----

- [x] Enable user to enter a search phrase which may contain **more
  than** one word.
- [x] Send the query to two search engines of your choice
  **concurrently** such as yahoo or bing.
- [x] Program will need to analyze the downloaded HTML source from
  the first 2 search results from each search engine. This will form
  the **seeds**.
- [x] Program will need to analyze the downloaded html source from
  the seeds to find out the top 10 unique webpages through
  multi-threading. You would have to look for the specific patterns to
  retrieve the web site address **eg <http://>....** as a pattern. You
  will need to apply suitable regular expressions to find such
  patterns.
- [x] Ignore advertisements when processing the web content.
- [x] Your application will create 2 **separate threads** which will
  **download** and process each web document to find the web URLs,
  which will be added to the Queue.
- [x] Each of these 10 webpages URLs will be added to an appropriate
  dAta structure such as Queue as they are found.
- [x] The 2 threads will process the next available and unprocessed
  wEb URL once it has finished its current task. They will keep doing
  so, until the 12 websites are found with its contents downloaded.
- [x] The 10 website URLs and its html page contents **(saved
  lOcally)** should be shown to the user through a GUI (For example,
  after clicking on a selected URL, the web page content will be shown
  in a text area). The website URLs are to be displayed in a list in
  ascending order.
- [x] Keep track of the number of occurrences of the search phrase
  wIthin the html page and display the number of occurrences.

Advanced
--------

You are encouraged to provide extra advanced features for your
application. Examples would be:

- [x] Allow user to specify the number of threads to download and
  process the web page source.
- [x] Compare performance of using say 1 vs 2 vs 4 threads, by
  keeping track of the total time to process requests.

