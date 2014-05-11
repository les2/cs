#!/bin/bash

set -e

function run_test {
  echo "test data: $@"
  java MaximumSubarray brute_force $@
  java MaximumSubarray brute_force_optimized $@
  java MaximumSubarray dp $@
}

run_test 35 -67 19 56 94 11 77 -68 74 -57
