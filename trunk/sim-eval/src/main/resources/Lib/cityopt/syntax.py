import __builtin__
from keyword import kwlist
import ast
from collections import defaultdict

__all__ = ['checkExpressionSyntax']

ERROR = 0
READY = 1
LATER = 2

def checkExpressionSyntax(expr, env, env_complete):
    if not env_complete:
        env = LooseGlobalEnv(env)
    try:
        t = ast.parse(expr, filename='<script>', mode='eval')
    except SyntaxError, e:
        return (e.lineno, e.offset, "invalid expression syntax")
    status, id, value = checkExpr(t, set(), env)
    if status == ERROR:
        return value
    else:
        return None

class LooseGlobalEnv(defaultdict):
    def __init__(self, known_globals):
        super(LooseGlobalEnv, self).__init__(LooseTopLevelObject, known_globals)

    def __contains__(self, name):
        return True

class LooseTopLevelObject:
    def __getattr__(self, name):
        if name == '__call__':
            raise AttributeError, name
        return LooseAttribute()

class LooseAttribute:
    def __getattr__(self, name):
        if name == '__call__':
            return dummy_function
        return LooseAttribute2()

class LooseAttribute2:
    def __getattr__(self, name):
        if name == '__call__':
            return dummy_function
        raise AttributeError, name

def dummy_function():
    pass

def message(node, text):
    return (node.lineno, node.col_offset, text)

def checkExpr(node, latebound, env):
    if isinstance(node, ast.Name):
        if isinstance(node.ctx, ast.Load):
            if node.id in latebound:
                return (LATER, node.id, None)
            elif node.id in env:
                return (READY, node.id, env[node.id])
        return (ERROR, None, 
                message(node, "name '%s' is not defined" % (node.id)))
    # Note Jython 2.5.3 bug: attribute nodes are not ast.Attribute instances
    elif isinstance(node, ast.Attribute) or node.__class__.__name__ == 'Attribute':
        vs, vi, vv = checkExpr(node.value, latebound, env)
        if vs == READY:
            if hasattr(vv, node.attr):
                return (READY, vi + "." + node.attr, getattr(vv, node.attr))
            else:
                return (ERROR, None,
                        message(node, "'%s' object %s has no attribute '%s'"
                                % (type(vv).__name__, vi, node.attr)))
        elif vs == LATER:
            return (LATER, vi + "." + node.attr, None)
        else:
            return (ERROR, vi, vv)
    elif isinstance(node, ast.Call):
        fs, fi, fv = checkExpr(node.func, latebound, env)
        if fs == READY:
            if not callable(fv):
                return (ERROR, None,
                        message(node, "'%s' object %s is not callable"
                                % (type(fv).__name__, fi)))
            for argnode in node.args:
                xs, xi, xv = checkExpr(argnode, latebound, env)
                if xs == ERROR:
                    return (xs, xi, xv)
            return (LATER, fi + "(...)", None)
        elif fs == LATER:
            return (LATER, fi + "(...)", None)
        else:
            return (ERROR, fi, fv)
    elif isinstance(node, ast.Lambda):
        inner = set(latebound)
        inner.update(arg.id for arg in node.args.args)
        if node.args.vararg:
            inner.add(node.args.vararg)
        if node.args.kwarg:
            inner.add(node.args.kwarg)
        bs, bi, bv = checkExpr(node.body, inner, env)
        if bs == ERROR:
            return (bs, bi, bv)
        else:
            return (LATER, "...", None)
    elif isinstance(node, ast.ListComp) or isinstance(node, ast.GeneratorExp):
        inner = set(latebound)
        for gen in node.generators:
            gs, gi, gv = checkExpr(gen.iter, inner, env)
            if gs == ERROR:
                return (gs, gi, gv)
            inner.add(gen.target.id)
            for fi in gen.ifs:
                cs, ci, cv = checkExpr(fi, inner, env)
                if cs == ERROR:
                    return (cs, ci, cv)
        es, ei, ev = checkExpr(node.elt, inner, env)
        if es == ERROR:
            return (es, ei, ev)
        else:
            return (LATER, "...", None)
    else:
        try:
            value = ast.literal_eval(node)
            return (READY, repr(value), value)
        except:
            for child in ast.iter_child_nodes(node):
                cs, ci, cv = checkExpr(child, latebound, env)
                if cs == ERROR:
                    return (cs, ci, cv)
            return (LATER, "...", None)

if __name__ == '__main__':
    env = dict(globals())
    env.update(__builtin__.__dict__)
    env_complete = True

    def assertRight(expr):
        assert checkExpressionSyntax(expr, env, env_complete) is None
    def assertWrong(expr, message):
        assert checkExpressionSyntax(expr, env, env_complete)[2] == message

    fooNotDefined = "name 'foo' is not defined"
    assertRight("1+1")
    assertWrong("foo", fooNotDefined)
    assertRight("checkExpressionSyntax")
    assertRight("ast.dump")
    assertWrong("ast.bar", "'module' object ast has no attribute 'bar'")
    assertRight("ast.dump(min, max, 3)")
    assertWrong("ast.dump(min, foo, 3)", fooNotDefined)
    assertWrong("foo(min, ast.dump, 3)", fooNotDefined)
    assertRight("[i+1 for i in range(10)]")
    assertRight("[i+1 for i in range(10) if i > 0 for j in range(10) if j < 0]")
    assertRight("(i+1 for i in range(10))")
    assertWrong("(i+foo for i in range(10))", fooNotDefined)
    assertRight("(i+foo for i in range(10) for foo in [1])")
    assertRight("checkExpressionSyntax('1+1', __builtin__.__dict__)")
    assertRight("lambda x : x + 1")
    assertWrong("lambda x : x + foo + 1", fooNotDefined)
    assertRight("lambda x, *foo : x + foo + 1")
    assertWrong("return 2", "invalid expression syntax")

    env = LooseGlobalEnv(env)
    env_complete = False
    assertRight("metric")
    assertRight("component.output.mean")
    assertWrong("foo.bar.baz.moo", "'instance' object foo.bar.baz has no attribute 'moo'")
    assertWrong("foo()", "'instance' object foo is not callable")
    assertRight("extParam.at()")
    assertRight("component.output.at()")
    assertWrong("foo.bar.baz.moo()", "'instance' object foo.bar.baz has no attribute 'moo'")
    assertRight("[i + metric for i in [1,2,3]]")
    assertRight("lambda x : x + foo + 1")
    print "All tests OK"
