factorial = function(n) {
  if (n == 0) {
    1
  } else {
    factorial(n - 1) * n
  }
}

print(factorial(5))
