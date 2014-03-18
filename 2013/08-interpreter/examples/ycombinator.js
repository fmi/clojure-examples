y = function(le) {
  (function(f) {
    f(f)
  })(function(f) {
    le(function(x) {
      (f(f))(x)
    })
  })
}

factorial = y(function(fact) {
  function(x) {
    if (x == 0) {
      1
    } else {
      fact(x - 1) * x
    }
  }
})

print(factorial(5))
