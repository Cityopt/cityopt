import __builtin__
from math import sqrt
from datetime import datetime
from array import array
from eu.cityopt.sim.eval import TimeSeries

__all__ = ['TimeSeries', 'totimestamp', 'integrate',
           'mean', 'stdev', 'var', 'min', 'max']

_epoch = datetime.fromtimestamp(0)

def _convertTimestampsToDatetimes(timestamps):
    return [datetime.fromtimestamp(t) for t in timestamps]

# Equivalent to timedelta.total_seconds() in Python 2.7
def _total_seconds(td):
    return td.seconds + td.days * 86400.0

# Similar to datetime.timestamp() in Python 3.3
def totimestamp(arg):
    if isinstance(arg, datetime):
        return _total_seconds(arg - _epoch)
    return array('d', (_total_seconds(d - _epoch) for d in arg))

def _convertToTimestamp(arg):
    if isinstance(arg, datetime):
        return _total_seconds(arg - _epoch)
    return float(arg)

def _convertToTimestamps(arg):
    try:
        return array('d', [_convertToTimestamp(arg)])
    except:
        return array('d', (_convertToTimestamp(a) for a in arg))

def integrate(ts, a, b, scale=1.0):
    return ts.internalFunction().integrate(
        _convertToTimestamp(a), _convertToTimestamp(b), scale)

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
