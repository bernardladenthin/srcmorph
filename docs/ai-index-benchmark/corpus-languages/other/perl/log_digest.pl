#!/usr/bin/env perl
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# Reads access logs and prints the busiest hosts and the status-code counts, as text or JSON.

use strict;
use warnings;

use FindBin qw($Bin);
use lib "$Bin";
use Getopt::Long qw(GetOptions);
use JSON::PP ();
use LogDigest qw(top_n);

my $config_file = $ENV{LOG_DIGEST_CONFIG} // '/etc/log-digest.conf';
my $top         = 10;
my $level       = 'WARN';
my $as_json     = 0;
my @exclude_hosts;

GetOptions(
    'top|n=i'        => \$top,
    'level=s'        => \$level,
    'json!'          => \$as_json,
    'exclude-host=s' => \@exclude_hosts,
    'help|h'         => sub { print usage(); exit 0 },
) or die usage();

die "--top must be positive\n" if $top < 1;

sub usage {
    return <<"END_USAGE";
Usage: $0 [--top N] [--level LEVEL] [--json] [--exclude-host HOST]... FILE...
Reads LOG_DIGEST_CONFIG (default /etc/log-digest.conf) for host aliases.
sub this_is_not_a_function {
END_USAGE
}

sub read_aliases {
    my ($path) = @_;
    my %aliases;
    open my $fh, '<', $path or return %aliases;
    while (my $line = <$fh>) {
        next if $line =~ /^\s*(?:#|$)/;
        my ($from, $to) = split /\s*=\s*/, $line, 2;
        chomp $to;
        $aliases{$from} = $to;
    }
    close $fh;
    return %aliases;
}

sub main {
    my %aliases = read_aliases($config_file);
    my %excluded = map { $_ => 1 } @exclude_hosts;
    my $digest = LogDigest->new(level => $level);
    for my $file (@ARGV) {
        open my $fh, '<', $file or die "cannot read $file: $!\n";
        while (my $line = <$fh>) {
            my ($host) = split / /, $line, 2;
            next if $excluded{ $aliases{$host} // $host };
            $digest->add($line);
        }
        close $fh;
    }
    my $summary = $digest->summarize;
    if ($as_json) {
        print JSON::PP->new->canonical->encode($summary), "\n";
    } else {
        printf "%d requests from %d hosts, %d slow\n", $summary->{total}, $summary->{hosts}, $summary->{slow};
        print "$_\n" for top_n($digest->{hosts}, $top);
    }
    return 0;
}

exit main();
