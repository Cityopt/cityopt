import __builtin__
from math import sqrt
from datetime import datetime
from array import array
from eu.cityopt.sim.eval import TimeSeries, Evaluator
from eu.cityopt.sim.eval.util import TimeUtils

__all__ = ['TimeSeries', 'todatetime', 'tosimtime', 'integrate',
           'mean', 'stdev', 'var', 'min', 'max',
           'MINUTE_S', 'HOUR_S', 'DAY_S', 'INFINITY',
           'Infinity', 'NaN']

_epoch = datetime.utcfromtimestamp(0)

MINUTE_S = 60
HOUR_S = 60 * MINUTE_S
DAY_S = 24 * HOUR_S
INFINITY = float('inf')
Infinity = float('inf')
NaN = float('nan')

# The time origin of the project for which the current evaluation is done.
def _timeOrigin():
    return Evaluator.getActiveTimeOrigin()

def _convertSimtimesToDatetimes(simtimes):
    return [datetime.utcfromtimestamp(_timeOrigin() + t) for t in simtimes]

# Equivalent to timedelta.total_seconds() in Python 2.7
def _total_seconds(td):
    return td.seconds + td.days * 86400.0

def todatetime(arg):
    try:
        return datetime.utcfromtimestamp(_timeOrigin() + arg)
    except TypeError:
        return _convertSimtimesToDatetimes(arg)

# Similar to datetime.timestamp() in Python 3.3
def tosimtime(arg):
    try:
        return _convertToSimtime(arg)
    except:
        return array('d', (_convertToSimtime(a) for a in arg))

def _convertToSimtime(arg):
    if isinstance(arg, datetime):
        return _total_seconds(arg - _epoch) - _timeOrigin()
    elif isinstance(arg, str) or isinstance(arg, unicode):
        return TimeUtils.parseISO8601(arg).toEpochMilli()*0.001 - _timeOrigin()
    return float(arg)

def _convertToSimtimes(arg):
    try:
        return array('d', [_convertToSimtime(arg)])
    except:
        return array('d', (_convertToSimtime(a) for a in arg))

def integrate(ts, a, b, scale=1.0):
    return ts.internalFunction().integrate(
        _convertToSimtime(a), _convertToSimtime(b), scale)

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
