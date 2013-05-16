cons = function(a, d) {
  function(f) { f(a, d) }
}

car = function(c) {
  c(function(a, d) { a })
}

cdr = function(c) {
  c(function(a, d) { d })
}

c = cons(1, 2)
print(car(c))
print(cdr(c))
