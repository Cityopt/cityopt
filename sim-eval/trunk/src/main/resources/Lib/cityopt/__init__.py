import __builtin__
from math import sqrt
from datetime import datetime
from eu.cityopt.sim.eval import TimeSeries

from keyword import kwlist
import ast

__all__ = ['mean', 'stdev', 'var', 'min', 'max']

def _convertTimestampsToDatetimes(timestamps):
    return [datetime.fromtimestamp(t) for t in timestamps]

def mean(x):
    if isinstance(x, TimeSeries):
        return x.mean
    else:
        return sum(x) / float(len(x)) if len(x) > 0 else 0.0

def stdev(x):
    if isinstance(x, TimeSeries):
        return x.stdev
    else:
        return sqrt(var(x))

def var(x):
    if isinstance(x, TimeSeries):
        return x.var
    else:
        n = float(len(x))
        if n < 2.0:
            return 0.0
        else:
            m = sum(x) / n
            return sum((v-m)**2 for v in x) / (n - 1.0)

def min(x, *rest, **kw):
    if len(rest) == 0 and isinstance(x, TimeSeries):
        return x.min
    else:
        return __builtin__.min(x, *rest, **kw)

def max(x, *rest, **kw):
    if len(rest) == 0 and isinstance(x, TimeSeries):
        return x.max
    else:
        return __builtin__.max(x, *rest, **kw)
