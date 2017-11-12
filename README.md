# Genome Coverage
Given some genomes and many _reads_, computes and displays how genomes are covered by the _reads_.

## Introduction

We start out with the input data: 

* a multi-FASTA file containing several genomes, and
* a FastQ file containing several thousand _reads_ (short DNA sequences)

The idea is to search for every exact occurrence of every _read_ in every genome, and then compute and display **genome coverage**. Genome coverage refers to how many sequences "cover" every position in a given genome. 

![Screenshot](/GenomeCoverage_screenshot.png)

## Features

* Overlapping _reads_ are allowed.
* Occurrence search methods can be plugged in at any time by extending the abstract class [PatternSearch](src/io/github/alexandra/zaharia/search/PatternSearch.java). For now, only two types of search are implemented: [_naïve_](https://github.com/alexandra-zaharia/genome-coverage/blob/master/src/io/github/alexandra/zaharia/search/NaivePatternSearch.java) search (slow) and [suffix array pattern search](https://github.com/alexandra-zaharia/genome-coverage/blob/master/src/io/github/alexandra/zaharia/search/SuffixArrayPatternSearch.java) (really fast). The search method can be selected at run time.
* The user can:
  * Save the coverage chart for any given genome.
  * Save the coverage charts for all genomes.
  * View a coverage chart in full screen.
  * Superimpose several coverage charts on a single graphic (and save this file).
  * Save _read_ occurrences in a parsable output file.
  * Get an orange "About" pop-up :-)
  * Get usage instructions (in French) :-)
  * See stack traces in a scrollable SWING panel along with a helpful error message, but of course there are never any errors :-)

## Running GenomeCoverage

GenomeCoverage can be compiled manually or run from the provided [JAR](/out/artifacts/GenomeCoverage_jar/GenomeCoverage.jar):

    java -jar GenomeCoverage.jar

## Test files

A multi-FASTA file containing 8 genomes of about 8,000 nucleotides each is provided: [HPV.fna](/res/HPV.fna)

A FastQ file containing 10,000 _reads_ of 100 nucleotides each is provided: [reads.fq](/res/reads.fq)

## Notes

* I wrote this project for an assignment in 2014 when I was a first year Master's student. Class, method and variable names are in English, but comments and documentation are in French.

* _Reads_ are considered error-free.

* This project uses [JFreeChart](http://www.jfree.org/jfreechart/).

* The suffix array implementation is based on an existing implementation by [Robert Sedgewick and Kevin Wayne](https://algs4.cs.princeton.edu/63suffix/SuffixArrayX.java.html).

* I used this [workaround on StackOverflow](https://stackoverflow.com/questions/18004150/desktop-api-is-not-supported-on-the-current-platform/18004334#18004334) for an issue with `java.awt.Desktop` failing to open an URL in the default web browser under Linux if `libgnome2-0` is not installed.
