# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
package LogDigest;

# Condenses web-server access logs into per-host and per-status counts.

use strict;
use warnings;

use POSIX qw(strftime);
use List::Util qw(max sum);
use Carp qw(croak);
use parent 'Exporter';

use constant {
    MAX_LINES     => 50_000,
    DEFAULT_LEVEL => 'WARN',
};
use constant SLOW_REQUEST_MS => 1500;

our $VERSION = '1.4';
our @EXPORT_OK = qw(parse_line top_n);

my %LEVEL_RANK = (DEBUG => 0, INFO => 1, WARN => 2, ERROR => 3);

sub new {
    my ($class, %args) = @_;
    my $level = $args{level} // DEFAULT_LEVEL;
    croak "unknown level '$level'" unless exists $LEVEL_RANK{$level};
    my $self = {
        level      => $level,
        hosts      => {},
        statuses   => {},
        slow       => 0,
        line_count => 0,
    };
    return bless $self, $class;
}

sub parse_line {
    my ($line) = @_;
    my ($host, $time, $method, $path, $status, $millis) =
        $line =~ m{^(\S+) \S+ \S+ \[([^\]]+)\] "(\w+) (\S+)[^"]*" (\d{3}) (\d+)$}
        or return;
    return {
        host   => _normalize_host($host),
        time   => $time,
        method => $method,
        path   => $path,
        status => $status,
        millis => $millis,
    };
}

sub add {
    my ($self, $line) = @_;
    croak 'more than ' . MAX_LINES . ' lines' if ++$self->{line_count} > MAX_LINES;
    my $entry = parse_line($line) or return 0;
    $self->{hosts}{ $entry->{host} }++;
    $self->{statuses}{ $entry->{status} }++;
    $self->{slow}++ if $entry->{millis} > SLOW_REQUEST_MS;
    return 1;
}

sub summarize {
    my ($self) = @_;
    my $total = sum(0, values %{ $self->{hosts} });
    return {
        generated => strftime('%Y-%m-%dT%H:%M:%SZ', gmtime),
        total     => $total,
        hosts     => scalar keys %{ $self->{hosts} },
        busiest   => max(0, values %{ $self->{hosts} }),
        slow      => $self->{slow},
        statuses  => { %{ $self->{statuses} } },
    };
}

sub top_n {
    my ($counts, $n) = @_;
    my @sorted = sort { $counts->{$b} <=> $counts->{$a} || $a cmp $b } keys %$counts;
    return @sorted[0 .. ($n < @sorted ? $n - 1 : $#sorted)];
}

sub _normalize_host {
    my ($host) = @_;
    $host = lc $host;
    $host =~ s/\.$//;
    return $host;
}

1;

__END__

=head1 NAME

LogDigest - per-host and per-status counts of an access log

=head1 SYNOPSIS

  my $digest = LogDigest->new(level => 'INFO');
  $digest->add($_) while <STDIN>;
  my $summary = $digest->summarize;

=head1 DESCRIPTION

sub not_a_sub { this is documentation, not code }

=cut
